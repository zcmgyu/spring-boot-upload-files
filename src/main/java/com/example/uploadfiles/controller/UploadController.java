package com.example.uploadfiles.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.uploadfiles.storage.StorageService;

@Controller
public class UploadController {

	@Autowired
	StorageService storageService;

	List<String> files = new ArrayList<>();

	@PostMapping("/uploads/new")
	public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
		String message;
		try {
			storageService.store(file);
			String fileName = file.getOriginalFilename();
			files.add(file.getOriginalFilename());

//			message = "You successfully uploaded " + file.getOriginalFilename() + "!";
			Map<String, String> response = new HashMap() {
				{
					put("url", "http://localhost:9292/uploads/images/" + fileName);
				}
			};
//			return ResponseEntity.status(HttpStatus.OK).body(response);
			return new ResponseEntity(response, new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
//			message = "FAIL to upload " + file.getOriginalFilename() + "!";
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

//	@GetMapping("/getallfiles")
//	public ResponseEntity<List<String>> getListFiles(Model model) {
//		List<String> fileNames = files
//				.stream().map(fileName -> MvcUriComponentsBuilder
//						.fromMethodName(UploadController.class, "getFile", fileName).build().toString())
//				.collect(Collectors.toList());
//
//		return ResponseEntity.ok().body(fileNames);
//	}

	@GetMapping("/uploads/images/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		Resource file = storageService.loadFile(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
}
