/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.gpeh;


import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.amanzi.awe.gpeh.parser.Events;
import org.amanzi.neo.core.utils.NeoTreeContentProvider;
import org.amanzi.neo.core.utils.NeoTreeElement;
import org.amanzi.neo.core.utils.NeoTreeLabelProvider;
import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.amanzi.neo.loader.ui.wizards.LoaderPage;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * <p>
 * GPEH loader second page
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class GpehImportWizardPage extends LoaderPage<CommonConfigData> {
    private static final Logger LOGGER = Logger.getLogger(GpehImportWizardPage.class);

    public  static final String SELECTED_EVENT = "GPEH SELECTED_EVENTS";

    private CheckboxTreeViewer viewer;

    private HashSet<Events> selectedEvents;

    public GpehImportWizardPage() {
        super("GpehAdditionalPage");
        setTitle(NeoLoaderPluginMessages.GpehOptionsTitle);
        setDescription(NeoLoaderPluginMessages.GpehOptionsDescr);

    }

    @Override
    public void createControl(Composite parent) {
        final Composite main = new Composite(parent, SWT.FILL);
notcompiletest!
        GridLayout mainLayout = new GridLayout(2, false);
        main.setLayout(mainLayout);

        Group mainFrame = new Group(main, SWT.FILL);
        mainFrame.setText("GPEH loading options");

        mainFrame.setLayout(mainLayout);

        viewer = new CheckboxTreeViewer(mainFrame);
        viewer.setLabelProvider(new NeoTreeLabelProvider());
        viewer.setContentProvider(new TreeContentProvider());
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1);
        layoutData.widthHint = 450;
        layoutData.heightHint = 200;
        viewer.getControl().setLayoutData(layoutData);

        setControl(main);
        setDefaults();
        addListeners();
        update();
    }

    private void setDefaults() {
        // TODO HARDCODING!!!

        Set<TreeElem> set = new LinkedHashSet<TreeElem>();
        TreeElem locationsGroup = new TreeElem(ElemType.GROUP, "Locations");
        // locationsGroup.setImage(NodeTypes.CITY.getImage());

        locationsGroup.addChield(new TreeElem(ElemType.EVENT, Events.findById(429).name(), Events.findById(429)));

        set.add(locationsGroup);
        // set.addAll(locationsGroup.getChildrens());

        TreeElem mReportGroup = new TreeElem(ElemType.GROUP, "Measurement Reports");
        // mReportGroup.setImage(NodeTypes.CALL_ANALYSIS.getImage());

        mReportGroup.addChield(new TreeElem(ElemType.EVENT, Events.RRC_MEASUREMENT_REPORT.name(), Events.RRC_MEASUREMENT_REPORT));
        mReportGroup.addChield(new TreeElem(ElemType.EVENT, Events.INTERNAL_RADIO_QUALITY_MEASUREMENTS_RNH.name(), Events.INTERNAL_RADIO_QUALITY_MEASUREMENTS_RNH));

        set.add(mReportGroup);
        // set.addAll(mReportGroup.getChildrens());

        viewer.setInput(set);

        viewer.setAllChecked(true);
        checkStateChange(locationsGroup);
        checkStateChange(mReportGroup);
    }

    private void addListeners() {
        viewer.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                TreeElem el = (TreeElem)event.getElement();
                checkStateChange(el);
            }
        });
    }

    private void checkStateChange(TreeElem el) {
        boolean state = viewer.getChecked(el);
        for (TreeElem chield : el.getChildrens()) {
            viewer.setChecked(chield, state);
        }
        Object[] checked = viewer.getCheckedElements();
        selectedEvents = new HashSet<Events>(checked.length);
        for (int i = 0; i < checked.length; i++) {
            Events eventId = ((TreeElem)checked[i]).getEventId();
            if (eventId != null)
                selectedEvents.add(eventId);
        }
        update();
    }


    public Set<Events> getSelectedEvents() {
        return selectedEvents;
    }

    private class TreeContentProvider extends NeoTreeContentProvider {

        private final LinkedHashSet<TreeElem> elements = new LinkedHashSet<TreeElem>();

        @Override
        public Object[] getElements(Object inputElement) {
            return elements.toArray(new TreeElem[0]);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            if (newInput == null) {
                elements.clear();
            } else {
                Set<TreeElem> input = (Set<TreeElem>)newInput;
                elements.clear();
                elements.addAll(input);
            }
        }

    }

    private static class TreeElem extends NeoTreeElement {
        private final ElemType type;
        private final String name;
        private final Set<TreeElem> childrens = new LinkedHashSet<TreeElem>();
        private TreeElem parent;
        private Events eventId;
        /**
         * @param node
         * @param service
         */
        public TreeElem(ElemType type, String name) {
            super(null, null);
            this.type = type;
            this.name = name;
        }

        /**
         * @param node
         * @param service
         */
        public TreeElem(ElemType type, String name, Events eventId) {
            super(null, null);
            this.type = type;
            this.name = name;
            this.eventId = eventId;
        }

        @Override
        public TreeElem[] getChildren() {
            return childrens.toArray(new TreeElem[0]);
        }

        @Override
        public TreeElem getParent() {
            return parent;
        }

        public void setParent(TreeElem parent) {
            this.parent = parent;
        }

        @Override
        public boolean hasChildren() {
            return /* type != ElemType.GROUP && */!childrens.isEmpty();
        }

        public Set<TreeElem> getChildrens() {
            return childrens;
        }

        public void addChield(TreeElem chield) {
            chield.setParent(this);
            childrens.add(chield);
        }

        @Override
        public String getText() {
            return name;
        }

        @Override
        public Image getImage() {
            return image;
        }

        @Override
        public void setImage(Image image) {
            this.image = image;
        }

        public Events getEventId() {
            return eventId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((parent == null) ? 0 : parent.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            TreeElem other = (TreeElem)obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (parent == null) {
                if (other.parent != null)
                    return false;
            } else if (!parent.equals(other.parent))
                return false;
            return true;
        }
    }

    private static enum ElemType {
        GROUP, EVENT;
    }

    @Override
    protected boolean validateConfigData(CommonConfigData configurationData) {
        if (selectedEvents.isEmpty()){
            return false;
        }
        configurationData.getAdditionalProperties().put(SELECTED_EVENT, new HashSet<Events>(selectedEvents));
        return true;
    }



}
