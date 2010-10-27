package com.osp.ide.properties;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.core.ModelManager;



public class ModelComp extends Composite {

	private TableViewer fViewer;
	private String fValue;
	private ModelItem[] fEntryNamesAndValues = new ModelItem[0];
	
	ModelListContentProvider contentProvider= null;
	ModelListLabelProvider   labelProvider = null;
	
	private ArrayList model_listeners;
	
	protected class ModelItem extends Object
	{
		String name;
		boolean bChecked=false;
		
		public ModelItem(String name)
		{
			this.name = name;
		}
		
		public void setChecked(boolean checked)
		{
			bChecked = checked;
		}
		
		public boolean getChecked()
		{
			return bChecked;
		}
		
		public String getName()
		{
			return name;
		}
	}	
	
	protected class ModelListContentProvider implements IStructuredContentProvider
	{
		Object[] elements=null;
		
		public Object[] getElements(Object inputElement) {
			// TODO Auto-generated method stub
			return elements;
		}

		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			elements = (Object[]) newInput;
		}
	}
	
	protected class ModelListLabelProvider extends LabelProvider
	{
		private ImageDescriptor img_check_desc = IdePlugin.getDefault().createImageDescriptor(IConstants.IMG_CHECK);
		private ImageDescriptor img_uncheck_desc = IdePlugin.getDefault().createImageDescriptor(IConstants.IMG_UNCHECK);
		
		private Image image_check=null;
		private Image image_uncheck=null;
		
		public ModelListLabelProvider() {
			if(img_check_desc != null && image_check == null)
				image_check = img_check_desc.createImage();
			if(img_uncheck_desc != null && image_uncheck == null)
				image_uncheck = img_uncheck_desc.createImage();
		}
		
		public String getText(Object element) {
			
			if( element != null && element instanceof ModelItem ) 
				return ((ModelItem)element).getName();
			
			return null;
		}
		
		public Image getImage(Object element) { 
			if( element != null && element instanceof ModelItem ) {
				if(((ModelItem)element).getChecked()) return image_check;
				
			}
			return image_uncheck; 
		}		
	}	
	
	
	public ModelComp(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        this.setLayout(layout);	
        this.setLayoutData(new GridData(GridData.BEGINNING));		

        
		fViewer = new TableViewer(this, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		contentProvider = new ModelListContentProvider();
		fViewer.setContentProvider(contentProvider);
			
		labelProvider = new ModelListLabelProvider();
		fViewer.setLabelProvider(labelProvider);
			
		fViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent e) {
                // will default to false until a selection is made
            	itemSelectChanged();
            }
        });
			
		fViewer.setInput(fEntryNamesAndValues);
			
		Table table =  fViewer.getTable();        
        
		GridData gd = new GridData(GridData.FILL_BOTH);
		
		gd.heightHint = 80;
//		gd.widthHint = 100;
		table.setLayoutData(gd);
		
		model_listeners = new ArrayList();
	}
	
	private void itemSelectChanged()
	{
		int index = fViewer.getTable().getSelectionIndex(); 
		if( index != -1 )
		{
			String value = fViewer.getTable().getItem(index).getText();
			updateListForValue(value);
		}
		fViewer.refresh();
	}
	
	private void updateListForValue(String value) {
		
		if( value == null || value.length()==0)
		{
			fValue = checkDefault();
		}
		else
		{
			if(checkItem(value))
			{
				fValue = value;
			}
			else
			{
				fValue = checkDefault();
			}
		}
		
		fireModleChanged(fValue);		
		
		fViewer.refresh();
	}
	
	
	private String findDefaultItem() {
		
		if(fViewer.getTable().getItemCount() <= 0 ) return "";
		
		TableItem items[] = fViewer.getTable().getItems();
		
		for( int i = 0; i < items.length; i++ )
		{
			if( items[i].getText().toLowerCase().equals("wave") )
			{
				return items[i].getText();
			}
		}
		
		for( int i = 0; i < items.length; i++ )
		{
			if( items[i].getText().toLowerCase().startsWith("wave_") )
			{
				return items[i].getText();
			}
		}
		
		for( int i = 0; i < items.length; i++ )
		{
			if( items[i].getText().toLowerCase().startsWith("scotia") )
			{
				return items[i].getText();
			}
		}	
		
		return items[0].getText();
	}
	
	private String checkDefault() {
		String value = findDefaultItem();
	
		if( checkItem(value) ) return value;
		
		return "";
	}
	
	private boolean checkItem(String text)	{
		
		boolean checked = false;
		if(fEntryNamesAndValues != null )
		{
			for( int i = 0; i <fEntryNamesAndValues.length; i++)
			{
				if(text.equals(fEntryNamesAndValues[i].getName())) {
					fEntryNamesAndValues[i].setChecked(true);
					checked = true;
				}
				else {
					fEntryNamesAndValues[i].setChecked(false);
				}
			}
		}
		return checked;
	}	

	public void setSDKPath(String path, String currModel)
	{
		if( currModel == null ) currModel = "";
		
		boolean bfoundCurrModel = false;

		String[] models = ModelManager.getModels(path);
		if(models ==null || models.length == 0 )
		{
			fEntryNamesAndValues = new ModelItem[0];
		}
		else
		{
			fEntryNamesAndValues = new ModelItem[models.length];
			 for(int i = 0; i < models.length; i++ )
			 {
				 fEntryNamesAndValues[i] = new ModelItem(models[i]);
				 if(currModel.length() > 0 && currModel.equals(fEntryNamesAndValues[i].getName()))
				 {
					 fEntryNamesAndValues[i].setChecked(true);
					 bfoundCurrModel = true;
				 }
			 }			
		}
		
		fViewer.setInput(fEntryNamesAndValues);
		
		if( bfoundCurrModel )
		{
			fValue = currModel;
		}
		else
		{
			fValue = checkDefault();
		}
		fireModleChanged(fValue);
		
		fViewer.refresh();

	}
	
	public String getModel()
	{
		return fValue;
	}

	public void addModelChangeListener(IModelChangerListner l)
	{
		model_listeners.add(l);	
	}
	
	public void removeModelChangeListener(IModelChangerListner l)
	{
		model_listeners.remove(l);
	}	
	
	public void fireModleChanged(String model)
	{
		for(Iterator i = model_listeners.iterator(); i.hasNext(); )
		{
			((IModelChangerListner)i.next()).modelChanged(model);
		}		
	}	
}
