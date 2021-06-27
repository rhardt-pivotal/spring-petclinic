/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@RestController
@RequestMapping("/api")
class OwnerRestController {

	private static final Logger log = LoggerFactory.getLogger(OwnerRestController.class.getName());

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	private final OwnerRepository owners;

	private VisitRepository visits;

	private final PetRepository pets;

	public OwnerRestController(OwnerRepository clinicService, VisitRepository visits, PetRepository pets) {
		this.owners = clinicService;
		this.visits = visits;
		this.pets = pets;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}


//
//	@GetMapping("/owners/new")
//	public String initCreationForm(Map<String, Object> model) {
//		Owner owner = new Owner();
//		model.put("owner", owner);
//		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
//	}
//
	@PostMapping("/owners/new")
	public Owner processCreationForm(@RequestBody @Valid Owner owner) {
			Owner saved = this.owners.save(owner);
			return saved;
	}


	@GetMapping(path = "/owners", produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<Owner> ownersJson() {
		Collection<Owner> ret =  this.owners.findAll();
		log.info(ret.toString());
		return ret;
	}


	@GetMapping(path = "/owners/ids", produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<Integer> allOwnerIds() {
		Collection<Owner> oret =  this.owners.findAll();
		Collection<Integer> ret = oret.stream().map(o -> o.getId()).collect(Collectors.toList());
		// log.info(ret.toString());
		return ret;
	}


	@GetMapping(path = "/pets/ids", produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<Integer> allPetIds() {
		Collection<Pet> pret =  this.pets.findAll();
		Collection<Integer> ret = pret.stream().map(o -> o.getId()).collect(Collectors.toList());
		// log.info(ret.toString());
		return ret;
	}


	@PostMapping("/visits/new")
	public Visit addVisit(@RequestBody @Valid Visit visit) {

		return visits.save(visit);
	}




//	@GetMapping("/owners/{ownerId}/edit")
//	public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, Model model) {
//		Owner owner = this.owners.findById(ownerId);
//		model.addAttribute(owner);
//		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
//	}
//
//	@PostMapping("/owners/{ownerId}/edit")
//	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result,
//			@PathVariable("ownerId") int ownerId) {
//		if (result.hasErrors()) {
//			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
//		}
//		else {
//			owner.setId(ownerId);
//			this.owners.save(owner);
//			return "redirect:/owners/{ownerId}";
//		}
//	}
//
//	/**
//	 * Custom handler for displaying an owner.
//	 * @param ownerId the ID of the owner to display
//	 * @return a ModelMap with the model attributes for the view
//	 */
	@GetMapping("/owners/{ownerId}")
	public Owner showOwner(@PathVariable("ownerId") int ownerId) {
		Owner owner = this.owners.findById(ownerId);
		return owner;
	}


	@PostMapping("/owners/{ownerId}/pets/new")
	public Pet newPet(@PathVariable("ownerId") int ownerId, @RequestBody @Valid Pet pet) {
		Owner owner = this.owners.findById(ownerId);
		pet.setOwner(owner);
		Pet savedPet = pets.save(pet);
		owner.addPet(savedPet);
		owners.save(owner);
		return savedPet;
	}

	@GetMapping("/pettypes")
	public Collection<PetType> getPetTypes() {
		return this.pets.findPetTypes();
	}


}
