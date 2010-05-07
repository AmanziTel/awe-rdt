require 'neo4j'

#TODO this code was copied from report plugin
module Neo4j
  module Relationships
    class NodeTraverser
      def stop_on(&proc)
        @stop_evaluator = StopEvaluator.new proc
        self
      end
    end

    class StopEvaluator
      include org.neo4j.graphdb.StopEvaluator
      def initialize(proc, raw = false)
        @proc = proc
        @raw = raw
      end

      def isStopNode( traversal_position )
        # if the Proc takes one argument that we give it the traversal_position
        result = if @proc.arity == 1
          # wrap the traversal_position in the Neo4j.rb TraversalPostion object
          @proc.call TraversalPosition.new(traversal_position)
        else # otherwise we eval the proc in the context of the current node
          # do not stop on the start node
          return false if traversal_position.isStartNode()
          eval_context = Neo4j::load_node(traversal_position.currentNode.getId, @raw)
          eval_context.instance_eval(&@proc)
        end

        # java does not treat nil as false so we need to do instead
        (result)? true : false
      end
    end
  end
end

def sites(options={})
  NodeSet.new filter($network_root_node,'site', options)
end

def sectors(options={})
  NodeSet.new filter($network_root_node,'sector', options)
end

def properties(options={})
  NodeSet.new filter($drive_root_node,'m', options)
end

def counters(options={})
  if (options.has_key? "oss") and  (options.has_key? "file")
    oss=options.delete("oss")
    file=options.delete("file")
    puts "oss #{oss}\nfile #{file}"
      oss_node=filter(Neo4j.ref_node,'oss', {"name"=>oss}).first
      puts oss_node
      counter_node=filter(oss_node,'file', {"name"=>file}).first
      traverser=filter(counter_node,'mv', options)
      traverser.stop_on {get_property('type')=='file'}
      NodeSet.new traverser
  else
    if !$counter_root_node.nil?
      traverser=filter($counter_root_node,'mv', options)
      traverser.stop_on {get_property('type')=='file'}
      NodeSet.new traverser
    end
  end
end

def events(options=nil)
  options ||= {$event_property =>true}
  unless options.is_a? Hash
    options = {$event_property => (options.is_a? Regexp) ? options :options.to_s}
  end
  NodeSet.new filter($drive_root_node,'m', options)
end

def filter(root_node,type_name,options={})
  root_node.outgoing(:CHILD, :NEXT).depth(:all).filter do
    node_properties = props # defined in Neo4j::NodeMixin
    if node_properties["type"]==type_name
      if options.length!=0
        result=true
        options.keys.each do |key|
          result&&=options[key]==true ? node_properties[key] : (options[key].is_a? Regexp) ? node_properties[key]=~ options[key]:node_properties[key]==options[key]
        end
        result
      else
        true
      end
    else
      false
    end
  end
end

class NodeSet
  def initialize(traverser)
    @traverser=traverser
  end

  def each
    @traverser.each{|node|yield node}
  end

  def count_internal
    num=0
    @traverser.each{|n| num+=1}
    num
  end

  def collect(*args)
    PropertyTable.new(self,*args)
  end

  def method_missing(method_id,*args)
    puts method_id
    PropertySet.new(self,method_id)
  end
end

class PropertySet
  def initialize(node_set,property)
    @node_set=node_set
    @property=property.to_s
  end

  def each
    @node_set.each do |node|
      yield node.props[@property]
    end
  end

  def count_internal
    num=0
    @node_set.each {|n|  num += 1  if(!n.props[@property].nil?)}
    num
  end

  def sum_internal
    num=0.0
    @node_set.each{|n|
      if !n.props[@property].nil?
        num+= n.props[@property]
      end
    }
    num
  end
end

class PropertyTable
  def initialize(node_set,*properties)
    @node_set=node_set
    @properties=properties
  end

  def count_internal
    num=0
    @node_set.each do |node|
      p_found=0
      @properties.each do |p|
        if(!node.props[p].nil?)
          p_found+=1
        else
          break
        end
      end
      num += 1 if @properties.length==p_found
    end
    num
  end

  def aggregate(property)
    aggregation=Aggregation.new(property)
    each do |values|
      aggregation.add(values.delete(property),values)
    end
    aggregation
  end

  def each
    #TODO print first 10
#    i=0
    @node_set.each do |node|
#      break if i>10
      props=Hash.new
      @properties.each do |p|
        value=node.props[p]
        if(!value.nil?)
          props[p]=value
        else
          break
        end
      end
      if !props.empty? and props.length==@properties.length
#        i+=1
        yield props
      end
    end
  end

  def to_s
    result=@properties.join("\t")
    each do |row|
      result+="\n"+row.values.join("\t")
    end
    result
  end
end

class Aggregation
  def initialize(property)
    @property=property
    @result=Hash.new
  end

  def add(value, other_values)
    if @result.has_key? value
      @result[value]=@result[value]<<other_values
    else
      @result[value]=((other_values.is_a? Array)? other_values:[other_values])
    end
  end

  def each
    @result.each do |aggr_obect,rows|
      yield aggr_obect,rows
    end
  end

  def to_s
    result=""
    @result.each do |aggr_obect,rows|
      result+=aggr_obect+":\n"
      rows.each do |row|
        row.each do |k,v|
          result+=k+"="+v.to_s+"\t"
        end
        result+="\n"
      end
      result+="\n"
    end
    result
  end
end