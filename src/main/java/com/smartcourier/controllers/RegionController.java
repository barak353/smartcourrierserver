package com.smartcourier.controllers;

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

import com.smartcourier.ABCalgorithm;
import com.smartcourier.beans.Courier;
import com.smartcourier.beans.Delivery;
import com.smartcourier.beans.Region;
import com.smartcourier.dao.RegionDao;
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
	
	@GetMapping("/getAll")
	public List<Region> getAllSalary(){
		return regionDao.findAll();
	}
	
	@ApiOperation(value="Create region", response= Iterable.class)
	@PutMapping("/create")
	public Region createRegion(@RequestBody Region region) {
		regionDao.save(region);
		return region;
	}

	
	@ApiOperation(value="Update region", response= Iterable.class)//Please use this to create new delivery (because every delivery have a region).
	@PutMapping("/update/{regionId}")
	public Region addDeliveryToRegion(@PathVariable(value = "regionId") Long regionId, @RequestBody Delivery delivery) {
		Region currentRegion = regionDao.findOne(regionId);
		if(currentRegion != null){
			currentRegion.getDelivery().add(delivery);
			regionDao.save(currentRegion);
			if( currentRegion.getDelivery().size()  > currentRegion.getThreshold() ) //If the number of deliveries in this region is higher then the region threshold, then run the distribution algorithm.
				beeColony.runABCalgorithm();
			return currentRegion;

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
	}}