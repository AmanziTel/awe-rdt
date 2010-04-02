require 'java'
require 'date'
require 'neo4j'

#require 'neo4j/auto_tx'
#require 'ruby/cell'
require 'ruby/amanzi_neo4j'
puts "require gis"
require 'ruby/gis'
puts "include classes"
include_class org.amanzi.neo.core.database.nodes.CellID
include_class org.amanzi.neo.core.service.NeoServiceProvider

include_class org.amanzi.awe.report.model.Report
include_class org.amanzi.awe.report.model.Chart
include_class org.amanzi.awe.report.model.ReportText
include_class org.amanzi.awe.report.model.ReportImage
include_class org.amanzi.awe.report.model.ReportTable
include_class org.amanzi.awe.report.charts.ChartType
include_class org.amanzi.awe.report.charts.EventDataset
include_class org.amanzi.awe.report.charts.Charts

include_class org.jfree.data.category.DefaultCategoryDataset;
include_class org.jfree.chart.plot.PlotOrientation
include_class org.jfree.chart.plot.Plot
include_class org.jfree.data.time.Millisecond
include_class org.jfree.data.time.TimeSeries
include_class org.jfree.data.time.TimeSeriesCollection

  puts "Starting service...."
  neo_service = NeoServiceProvider.getProvider.getService
  database_location = NeoServiceProvider.getProvider.getDefaultDatabaseLocation
  Neo4j::Config[:storage_path] = database_location
  Neo4j::start(neo_service)
  puts "Service started"
  module NodeUtils
    def children_of(parent_id)
      begin
        Neo4j::Transaction.run {
          Neo4j.load(parent_id).relationships.outgoing(:CHILD).nodes
        }
      rescue Exception =>exc
        puts "children_of: an exception occured #{exc}"
        nil
      end
    end

    def find_dataset(dataset_name)
      traverser=Neo4j.ref_node.outgoing(:CHILD).depth(1).filter      do
        get_property(:name.to_s)== dataset_name
      end
      traverser.first
    end

    def find_aggr_node(dataset_node,property, distribute, select)
      traverser=dataset_node.outgoing(:AGGREGATION).depth(1).filter do
        prop_name=get_property('name')
        prop_distr=get_property('distribute')
        prop_select=get_property('select')
        prop_name==property and prop_distr==distribute and prop_select==select
      end
      traverser.first
    end

    def create_chart_dataset_aggr(aggr_node,type=:bar)
      ds=DefaultCategoryDataset.new()
      aggr_node.outgoing(:CHILD).depth(:all).each do |node|
        #      puts "count_node: #{node[:name]} -  #{node[:name]} - #{node[:value]}"
        ds.addValue(java.lang.Double.parseDouble(node[:value].to_s), "name", node[:name]);
      end
      ds
    end

    def create_chart_dataset(nodes,category,values,type=:bar)
      if type==:bar
        ds=DefaultCategoryDataset.new()
        nodes.each do |node|
          values.each do |value|
            ds.addValue(java.lang.Double.parseDouble(node[value].to_s), value, node[category].to_s);
          end
        end
     elsif type==:pie
        ds=DefaultPieDataset.new()
        nodes.each do |node|
          values.each do |value|
            ds.setValue(node[category].to_s,java.lang.Double.parseDouble(node[value].to_s));#TODO may be value? 
          end
        end
      end
      ds
    end
    
    def create_time_chart_dataset(nodes,name,time_period,time_property,value)
      ds=TimeSeriesCollection.new()
      series=TimeSeries.new(name)
      puts "create_time_chart_dataset: #{name} : #{value}"
      nodes.each do |node|
        if time_period==:millisecond
          if node.property? value
          puts "#{node.neo_id} - #{node[time_property]} - #{node[value]}"
            series.addOrUpdate(Millisecond.new(java.util.Date.new(node[time_property])), java.lang.Double.parseDouble(node[value].to_s));
          end
        end
      end
      ds.addSeries(series)
      ds
    end
    
    def create_event_chart_dataset(nodes,name,time_period,time_property,event_property,event_value)
      ds=EventDataset.new(name,event_value)
      nodes.each do |node|
        if time_period==:millisecond
          if node.property? event_property
            puts "event found: #{node.neo_id} - #{node[time_property]} - #{node[event_property]}"
            ds.addEvent(node[event_property], Millisecond.new(java.util.Date.new(node[time_property])))
          end
        end
      end
      ds
    end
    
    def  get_sub_nodes_with_props(parent, node_type, relation, time)
      time_format = '%H:%M:%S'
      parent.outgoing(relation).depth(:all).stop_on do
        prop_time=get_property(Java::org.amanzi.neo.core.INeoConstants::PROPERTY_TIME_NAME)
        #      puts "#{node_type}.props #{props}"
        if prop_time.nil?
          false
        else
          t=DateTime.strptime(prop_time,time_format)
          Time.gm(t.year,t.mon,t.day,t.hour,t.min,t.sec)>=time
        end
      end
    end
  end

  class Search
    attr_accessor :name
    def initialize(name,params)
      @name=name
      @traversers=[Neo4j.ref_node.outgoing(:CHILD).depth(1)]
      @params=params
    end

    def traverse(direction, relation, depth)
      new_traversers=[]
      if direction==:outgoing
        @traversers.each do |traverser|
          new_traversers<<traverser.outgoing(relation).depth(depth)
        end
      elsif direction==:incoming
        @traversers.each do |traverser|
          new_traversers<<traverser.incoming(relation).depth(depth)
        end
      else #both
        @traversers.each do |traverser|
          new_traversers<<traverser.both(relation).depth(depth)
        end
      end
      @traversers=new_traversers
    end

    def filter(&block)
      new_traversers=[]
      @traversers.each do |traverser|
        new_traversers<<traverser.filter(&block)
      end
      @traversers=new_traversers
    end

    def stop(&block)
      new_traversers=[]
      @traversers.each do |traverser|
        new_traversers<<traverser.stop_on(&block)
      end
      @traversers=new_traversers
    end

    def parent(&block)
      self.instance_eval &block
      new_traversers=[]
      @traversers.each do |traverser|
        traverser.each do |node|
          new_traversers<<node
        end
      end
      @traversers=new_traversers
    end

    def where(&block)
      filter(&block)
    end

    def from(&block)
      parent(&block)
    end

    def each
      @traversers.each do |traverser|
        traverser.each do |node|
          yield node
        end
      end
    end
  end

  class CellID
    attr_writer :beginRange
    attr_writer :endRange
    def initialize(name)
      @fullID = name
    end

    #
    # Returns a succesor of a CellID
    #
    def succ
      if self.rowIndex<@endRange.rowIndex
        cellId=CellID.new(rowIndex+1,columnIndex)
        cellId.beginRange=@beginRange
        cellId.endRange=@endRange
        cellId
      else
        cellId=CellID.new(@beginRange.rowIndex,columnIndex+1)
        cellId.beginRange=@beginRange
        cellId.endRange=@endRange
        cellId
      end
    end

    #
    # Compares two cellIDs
    #
    def <=>(value)
      if @beginRange.nil?
        @beginRange=self
      end
      if @endRange.nil?
        @endRange=value
      end
      if self.rowIndex!=value.rowIndex
        self.rowIndex<=>value.rowIndex
      else
        self.columnIndex<=>value.columnIndex
      end
    end
  end

  class Report
    include NodeUtils
    attr_reader :name
    attr_accessor :date,:author
    def initialize(name)
      @name = name
    end

    def setup(&block)
      self.instance_eval &block
      self
    end

    def author (new_author)
      setAuthor(new_author)
    end

    def date (new_date)
      setDate(new_date)
    end

    def text (new_text)
      addPart(ReportText.new(new_text))
    end

    def image (image_file_name)
      addPart(ReportImage.new(image_file_name))
    end

    def table (title,&block)
      currTable=ReportTable.new(title)
      currTable.setup(&block)
      addPart(currTable)
    end

    def chart(name,&block)
      begin
        currChart=Chart.new(name)
        currChart.setup(&block)
        addPart(currChart)
      rescue =>e
        puts "[Report.chart] An exception occured: #{e}"
      end
    end
  end

  class ReportTable
    include NodeUtils

    attr_accessor :title, :properties, :nodes, :sheet, :range
    def initialize(title)
      self.title = title
    end

    def setup(&block)
      self.instance_eval &block if block_given?
      get_data
    end

    def  get_data
      Neo4j::Transaction.run {
        if !@nodes.nil?
          #      puts @nodes.class
          begin
            @nodes.each do |n|
              n=Neo4j.load_node(n) if n.is_a? Fixnum
              @properties=n.props.keys if @properties.nil?
              row=Array.new
              @properties.each do |p| 
                if p!="id"
                row<<if n.property? p then n.get_property(p).to_s else "" end 
                else 
                  row<<n.neo_id.to_s 
                end
            end
              addRow(row.to_java(java.lang.String))
            end
            setHeaders(@properties.to_java(java.lang.String)) if @properties.is_a? Array
          rescue =>e
            puts "An exception occured #{e}"
          end
        elsif !@sheet.nil?
          sheetName=@sheet
          sheetNodes=Neo4j.load_node($RUBY_PROJECT_NODE_ID).outgoing(:SPREADSHEET).depth(:all).filter      do
            get_property('name')== sheetName
          end
          range=@range
          columnNodes=sheetNodes.first.outgoing(:COLUMN).depth(1).filter do
            #          puts "sheetNode.traverse.outgoing(:COLUMN) #{get_property(:name)}"
            puts get_property(:name)
            index=CellID.getColumnIndexFromCellID(get_property(:name))
            puts "index #{index}"
            index>=range.begin.getColumnIndex() and index<=range.end.getColumnIndex()
          end
          columnNodes.each do |col|
            puts "---> col #{col}"
            cells=col.traverse.outgoing(:COLUMN_CELL).depth(1).filter do
              name=relationship(:ROW_CELL,:incoming).start_node[:name]
              puts "cells row name #{name}"
              rowIndex=name.to_i
              rowIndex>=range.begin.getRowName().to_i and rowIndex<=range.end.getRowName().to_i
              true
            end
            cells.each {|cell| puts "cell #{cell}"}
          end
          #      TODO traverse rows and columns

        end
      }
    end

    def select(name,params,&block)
      Neo4j::Transaction.run {
        begin
#          puts "======> [ReportTable.select]"
          nodes=Search.new(name,params)
          nodes.instance_eval &block
          @properties=params[:properties]if !params.nil?  #TODO  
          nodes.each do |n|
            properties=n.props.keys if @properties.nil?
            row=Array.new
            @properties.each do |p|
              if p!="id"
                row<< if n.property? p then n.get_property(p).to_s else "" end 
              else
                row<<n.neo_id.to_s
              end
            end
            addRow(row.to_java(java.lang.String))
          end
          setHeaders(@properties.to_java(java.lang.String)) if @properties.is_a? Array
        rescue =>e
          puts e
        end
      }
    end

  end

  class Chart
    include NodeUtils

    attr_accessor :title, :type, :orientation, :domain_axis, :range_axis
    attr_writer :categories,:values, :nodes, :statistics
    attr_writer:property, :distribute, :select
    attr_writer :drive, :event, :property1, :property2, :start_time, :length
    def initialize(title)
      @datasets=[]
      self.title = title
    end

    def sheet=(sheet_name)
      @sheet=sheet_name
    end

    def select(name,params,&block)
      Neo4j::Transaction.run {
        begin
          nodes=Search.new(name,params)
          nodes.instance_eval &block
          if @type==:time
            event=params[:event]
            if !event.nil?
              #assume that we have one value
              @datasets<< create_event_chart_dataset(nodes,name,params[:time_period],params[:categories],params[:values],event)
            else
              params[:values].each do |value|
                @datasets<< create_time_chart_dataset(nodes,value,params[:time_period],params[:categories],value)
              end
            end
          else
            if params[:values].is_a? String
              #            setDataset(create_chart_dataset(nodes,params[:categories],[params[:values]]))
              @datasets<<create_chart_dataset(nodes,params[:categories],[params[:values]])
            elsif params[:values].is_a? Array
              @datasets<<create_chart_dataset(nodes,params[:categories],params[:values])
              #            setDataset(create_chart_dataset(nodes,params[:categories],params[:values]))
            else
              puts "Error: Only Strings or Arrays of Strings are supported for chart values!"
            end
          end
        rescue =>e
          puts e
        end
      }
    end

    def setup(&block)
      self.instance_eval(&block) #if block_given?
      Neo4j::Transaction.run {
        begin
          #JFreeChart specific settings
          if !@orientation.nil?
            if @orientation==:vertical
              setOrientation(PlotOrientation::VERTICAL)
            elsif @orientation==:horizontal
              setOrientation(Java::org.jfree.chart.plot.PlotOrientation::HORIZONTAL)
            end
          end
          setDomainAxisLabel(@domain_axis) if !@domain_axis.nil?
          setRangeAxisLabel(@range_axis) if !@range_axis.nil?
          @type=:bar if @type.nil?
          setChartType(ChartType.value_of(@type.to_s.upcase))
          #
          if !@sheet.nil?
          setCategories(@categories.begin,@categories.end)if !@categories.nil?
          setValues(@values.begin,@values.end) if !@values.nil?
          setSheet(@sheet)
          elsif !@nodes.nil?
            setNodeIds(@nodes.to_java(java.lang.Long)) if @nodes.is_a? Array
            setCategoriesProperty(@categories) if !@categories.nil? and  @categories.is_a? String
            setValuesProperties(@values.to_java(java.lang.String)) if !@values.nil? and   @values.is_a? Array
            #            setDataset(create_chart_dataset(@nodes,@categories,@values))
            @datasets<<create_chart_dataset(@nodes,@categories,@values)
          elsif !@statistics.nil?
            dataset_node=find_dataset(@statistics)
            if !@property.nil? and !@distribute.nil? and !@select.nil?
              aggr_node=find_aggr_node(find_dataset(@statistics),@property,@distribute,@select)
#                            setDataset(create_chart_dataset_aggr(aggr_node))
              @datasets<<create_chart_dataset_aggr(aggr_node)
            end
          end
          
          if @type==:time
            plot=Java::org.jfree.chart.plot.XYPlot.new
            for i in 0..@datasets.size-1
              puts "i=#{i} #{@datasets[i]}" #TODO delete debug info
              Charts.applyDefaultSettings(plot,@datasets[i],i)
            end
            Charts.applyMainVisualSettings(plot, getRangeAxisLabel(),getDomainAxisLabel(),getOrientation())
          elsif @type==:bar
            plot=Java::org.jfree.chart.plot.CategoryPlot.new
            plot.setDataset(@datasets[0])
          end
            setPlot(plot)
        rescue =>e
          puts "An exception occured during the chart setup: #{e}"
        end
      }
      self
    end
    #  transactional :find_dataset, :find_aggr_node, :create_chart_dataset
  end

  def method_missing(method_id, *args)
    if method_id.to_s =~ /([a-z]{1,3})([0-9]+)/
      CellID.new(method_id.to_s)
    else
      puts "report.rb: Unknown method: #{method_id}"
      super.method_missing(method_id.to_s, *args)
    end
  end

  def report (name, &block)
    begin
      report=Report.new(name)
      report.setup(&block)
      $report_model.updateReport(report)
    rescue =>e
      puts e
    end
    #  report
  end

  def chart(name,&block)
    begin
      puts "module method called"
      currChart=Chart.new(name)
      currChart.setup(&block)
      $report_model.createPart(currChart)
    rescue =>e
      puts "[chart(name,&block)] an exception occured: #{e}"
    end
  end

  def text (new_text)
    $report_model.createPart(ReportText.new(new_text))
  end

  def image (image_file_name)
    $report_model.createPart(ReportImage.new(image_file_name))
  end
