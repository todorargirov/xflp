package xf.xflp;

import xf.xflp.base.XFLPModel;
import xf.xflp.base.XFLPParameter;
import xf.xflp.base.XFLPSolution;
import xf.xflp.base.fleximport.ContainerData;
import xf.xflp.base.fleximport.FlexiImporter;
import xf.xflp.base.fleximport.InternalItemData;
import xf.xflp.base.fleximport.ItemData;
import xf.xflp.base.monitor.StatusCode;
import xf.xflp.base.monitor.StatusManager;
import xf.xflp.base.monitor.StatusMonitor;
import xf.xflp.base.problem.Container;
import xf.xflp.base.problem.Item;
import xf.xflp.opt.XFLPOptType;
import xf.xflp.report.LPReport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** 
 * Copyright (c) 2012-present Holger Schneider
 * All rights reserved.
 *
 * This source code is licensed under the MIT License (MIT) found in the
 * LICENSE file in the root directory of this source tree.
 *
 *
 * XFLP is central user interface for this suite. 
 * It combines all methods for data import, optimization execution, parameters and
 * retrieval of solutions.
 * 
 * The modeling of this class represents a state machine, where iteratively several 
 * methods must be called. The execution method take all inserted data and parameters
 * and start the optimizers.
 * 
 * 
 * @author hschneid
 *
 */
public class XFLP {

	/* Importer and data warehouse */
	private final FlexiImporter importer = new FlexiImporter();

	/* Optimization type - Chosen by user */
	private XFLPOptType optType;
	
	/* Last created solution */
	private XFLPSolution lastSolution;

	/* Shall be the metric transformed into a faster access metric */
	private final XFLPParameter parameter = new XFLPParameter();

	/* Manages internal status messages to external observer */
	private final StatusManager statusManager = new StatusManager();

	/**
	 * Calculates the Loading Problem with the before inserted data
	 * by addContainer() and addItem()
	 */
	public void executeLoadPlanning() throws XFLPException {
		statusManager.fireMessage(StatusCode.RUNNING, "XFLP started");

		// Flush import buffer
		importer.finishImport();

		// Init a planning model
		XFLPModel model = init();
		
		// Optimize the model
		optType.createInstance().execute(model);

		// Build solution object
		lastSolution = new XFLPSolution(model, importer.getDataManager());

		statusManager.fireMessage(StatusCode.FINISHED, "XFLP finished sucessfully.");
	}

	/**
	 * Transforms the read data into a model, which can be used
	 * for optimization.
	 * 
	 * @return Returns a model, which can be used for optimization procedures.
	 * @throws XFLPException
	 */
	private XFLPModel init() throws XFLPException {
		statusManager.fireMessage(StatusCode.RUNNING, "Initialisation");

		// Check input data
		if(importer.getItemList().isEmpty()) {
			statusManager.fireMessage(StatusCode.ABORT, "No items are given.");
			throw new XFLPException("No items are given.");
		}
		if(importer.getContainerList().isEmpty()) {
			statusManager.fireMessage(StatusCode.ABORT, "No container information were set.");
			throw new XFLPException("No container information were set.");
		}

		// Copy imported data to internal data structure
		// Container
		List<Container> containerTypeList = importer.getConvertedContainerList();
		// Items
		List<Item> itemList = new ArrayList<>();
		{
			// Transform imported items into loading and unloading items
			List<InternalItemData> itemDataList = importer.getItemList();
			
			for (InternalItemData iD : itemDataList) {
				itemList.add(iD.createLoadingItem(importer.getDataManager()));
				if(iD.getUnloadingLocation().length() > 0)
					itemList.add(iD.createUnLoadingItem(importer.getDataManager()));
			}
		}
		
		// Pre-Sort items for logical order (ascending order location index)
		itemList.sort(Comparator.comparingInt(arg0 -> arg0.loadingLoc));

		return new XFLPModel(itemList.toArray(new Item[0]), containerTypeList.toArray(new Container[0]), parameter);
	}

	/**
	 * Uses the last planned solution and turns it
	 * into a report representation.
	 * 
	 * All loading plan informations can be akquired by this report.
	 * 
	 * @return A report data structure with detailed information about the loading plan or null if no solution was calculated.
	 */
	public LPReport getReport() {
		if(lastSolution != null)
			return lastSolution.getReport();
		
		return null;
	}

	/**
	 * Returns true, if the last calculated solution contains
	 * unplanned items.
	 *
	 * This function can used testing the result for validity,
	 * instead of report generation, which is costly.
	 *
	 * @return
	 *         true, if there are unplanned items
	 *         false, if all items could be planned into container.
	 */
	public boolean hasUnplannedItems() {
		if(lastSolution != null && lastSolution.getModel() != null) {
			return lastSolution.getModel().getUnplannedItems().length != 0;
		}
		return false;
	}

	/**
	 * Akquire a item data object to insert data for
	 * a new item. The next call of this method will
	 * finalize the before akquired item data object.
	 * 
	 * @return Item data object
	 */
	public ItemData addItem() {
		return importer.getItemData();
	}

	/**
	 * Akquire a container data object to insert data for
	 * a new container. The next call of this method will
	 * finalize the before akquired container data object.
	 * 
	 * @return Container data object
	 */
	public ContainerData addContainer() {
		return importer.getContainerData();
	}

	/**
	 * Clears all added items.
	 */
	public void clearItems() {
		importer.clearItems();
	}

	/**
	 * Removes all inserted containers and reset the planning parameters to default.
	 */
	public void clearContainers() {
		importer.clearContainers();
	}

	/**
	 * All parameters are reset to default values
	 */
	public void clearParameters() {
		parameter.clear();
	}

	/**
	 * With this method an app can insert a specified status monitor object, where
	 * messages from the optimization are communicated.
	 * 
	 * A full transparent information flow is not given, because the loss of speed is huge.
	 * 
	 * @param monitor defined monitor object
	 */
	public void setStatusMonitor(StatusMonitor monitor) {
		statusManager.addObserver(monitor);
	}

	/**
	 * Specify the type of optimization
	 *
	 * Examples:
	 *  - Single Bin Packer
	 *  - Multi Bin Packer
	 *
	 * @param optType - Enum of available optimization types
	 */
	public void setTypeOfOptimization(XFLPOptType optType) {
		this.optType = optType;
	}

	public XFLPParameter getParameter() {
		return parameter;
	}
}
