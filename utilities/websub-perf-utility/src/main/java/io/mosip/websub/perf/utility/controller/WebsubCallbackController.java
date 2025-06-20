package io.mosip.websub.perf.utility.controller;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.websub.perf.utility.dto.RequestDTO;
import io.mosip.websub.perf.utility.dto.ResultMetadata;
import io.mosip.websub.perf.utility.service.WebsubCallbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "websubutility", description = "Operation related to websub performence")
public class WebsubCallbackController {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebsubCallbackController.class);
	
	@Autowired
	private WebsubCallbackService websubCallbackService;


	@Operation(summary = "Endpoint for websub callback", description = "Endpoint for websub callback", tags = { "websubutility" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success or you may find errors in error array in response"),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PostMapping(value = "/callback/**")
	public  ResponseEntity<Void> callbackHandler(@RequestBody RequestDTO requestDTO,HttpServletRequest httpServletRequest) {
		String subID=httpServletRequest.getRequestURI().split("/callback/")[1];
	    if(subID.contains("/")) {
	    	subID= subID.split("/")[0];
	    }
		try {
			websubCallbackService.compute(requestDTO,subID);
		} catch(Exception e) {
			LOGGER.error("Error occured while computing response times request object " + 
				subID + " " + requestDTO.getTimestamp() + " " + requestDTO.getMessageString(), e);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	@Operation(summary = "Endpoint for get result for subID", description = "Endpoint for get result for subID", tags = { "websubutility" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success or you may find errors in error array in response"),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@GetMapping(value = "/result/{subID}")
	public  ResponseEntity<ResultMetadata> getResult(@PathVariable String subID) {
		return new ResponseEntity<>(websubCallbackService.getResult(subID),HttpStatus.OK);
	}
	
	@Operation(summary = "Endpoint for reset cache", description = "Endpoint for reset cache", tags = { "websubutility" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success or you may find errors in error array in response"),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@GetMapping(value = "/reset")
	public  ResponseEntity<Void> reset() {
		
		websubCallbackService.reset();
		return new ResponseEntity<>(HttpStatus.OK);
	}

	
	
}
