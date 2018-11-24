package com.smartcourier.controllers;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcourier.beans.Courier;
import com.smartcourier.beans.Delivery;
import com.smartcourier.beans.Region;
import com.smartcourier.dao.CourierDao;
import com.smartcourier.dao.DeliveryDao;
import com.smartcourier.dao.RegionDao;

import ABCalgorithm.ABCalgorithm;
import ABCalgorithm.Distribution;
import ABCalgorithm.Division;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "/region")
@Api(value="Region Management")
public class RegionController {

	public static final Logger logger = LoggerFactory.getLogger(RegionController.class);
	private ABCalgorithm beeColony = new ABCalgorithm();	

	@Autowired
	RegionDao regionDao;
	
	@Autowired
	CourierDao courierDao;
		
	@Autowired
	DeliveryDao deliveryDao;
	
	@GetMapping("/getAll")
	public List<Region> getAllSalary(){
		List<Region> regions = regionDao.findAll();
		return regions;
	}
	
	@ApiOperation(value="Create region", response= Iterable.class)
	@PutMapping("/create")
	public Region createRegion(@RequestBody Region region) {
		regionDao.save(region);
		return region;
	}

	
	@ApiOperation(value="Update region", response= Iterable.class)//Please use this to create new delivery (because every delivery have a region).
	@PutMapping("/update/{regionId}")
	public Region addDeliveryToRegion(@PathVariable(value = "regionId") Long regionId, @RequestBody Delivery delivery) throws Exception 
	{
		Region region = regionDao.findOne(regionId);
		if(region != null)
		{
			delivery.setRegion(region);
			delivery.setType(0);//Deliveries that have not yet been assigned to a courier because they have not yet been distributed by the algorithm.
			deliveryDao.save(delivery);
			Region savedRegion = regionDao.findOne(regionId);
			if( ( savedRegion.getDelivery().size()  > savedRegion.getThreshold() ) && ( savedRegion.getCourier().size() > 0 )) //If the number of deliveries in this region is higher then the region threshold, then run the distribution algorithm.
			{
				ArrayList<Delivery> deliveriesToDistributeInRegion = new ArrayList<Delivery>(deliveryDao.findByRegionAndType(savedRegion,0));
			     deliveriesToDistributeInRegion.addAll((ArrayList<Delivery>) deliveryDao.findByRegionAndType(savedRegion,1));
				 Distribution distribution = beeColony.runABCalgorithm(savedRegion, deliveriesToDistributeInRegion);
				 //Save result from ABCalgorithm to DB.
				 for(Division division: distribution.getDivisions())
				 {
					 Courier courier = division.getCourier();
					 for(Delivery deliveryToCourier: division.getDeliveries())
					 {
						 deliveryToCourier.setCourier(courier);
						 deliveryDao.delete(deliveryToCourier);
						 deliveryDao.save(deliveryToCourier);
					 }
				 }
			}
			return savedRegion;
		} else{
			return null;
		}
	}
	
	@ApiOperation(value="Update region", response= Iterable.class)//Please use this to create new delivery (because every delivery have a region).
	@PutMapping("/update/{regionId}/{courierId}")
	public Region assignCourierToRegion(@PathVariable(value = "regionId") Long regionId, @PathVariable(value = "courierId") Long couriderId) {
		Region region = regionDao.findOne(regionId);
		Courier courier = courierDao.findOne(couriderId);
		if(region != null && courier != null){
			region.getCourier().add(courier);
			regionDao.save(region);
			return region;
		} else{
			return null;
		}
	}
	
	@ApiOperation(value="Delete region", response= Iterable.class)
	@DeleteMapping("/delete/{regionId}")
	public Boolean deleteRegion(@PathVariable(value = "regionId") Long regionId) {
		//Region will be deleted only if it have 0 deliveries.
		Region currentRegion = regionDao.findOne(regionId);
		if(currentRegion != null){
			if(currentRegion.getDelivery().size() <= 0)
			{
				regionDao.delete(currentRegion);
				return true;
			}
		return false;
		} else{
			return false;
		}
	}
	
	@ApiOperation(value="Delete delivery", response= Iterable.class)
	@DeleteMapping("/delete/{regionId}/{deliveryId}")
	public Boolean deleteDeliveryFromRegion(@PathVariable(value = "regionId") Long regionId, @PathVariable(value = "deliveryId") Long deliveryId) {
		//Region will be deleted only if it have 0 deliveries.
		Region currentRegion = regionDao.findOne(regionId);
		if(currentRegion != null)
		{
			if(currentRegion.getDelivery().size() > 0)
			{
				for(int i = 0; i < currentRegion.getDelivery().size(); i++)
				{
					Delivery delivery = currentRegion.getDelivery().get(i);
					if((long)delivery.getId() == (long)deliveryId)
					{
						currentRegion.getDelivery().remove(i);
						regionDao.save(currentRegion);
						deliveryDao.delete(delivery);
						return true;
					}
				}
			}
		}
		return false;
	}
}
