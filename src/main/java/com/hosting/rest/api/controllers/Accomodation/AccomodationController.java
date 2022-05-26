package com.hosting.rest.api.controllers.Accomodation;

import static com.hosting.rest.api.Utils.PaginationConstants.DEFAULT_PAGE_NUMBER;
import static com.hosting.rest.api.Utils.PaginationConstants.DEFAULT_PAGE_SIZE;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hosting.rest.api.exceptions.IllegalArguments.IllegalArgumentsCustomException;
import com.hosting.rest.api.models.Accomodation.AccomodationCategoryModel;
import com.hosting.rest.api.models.Accomodation.AccomodationModel;
import com.hosting.rest.api.models.Accomodation.AccomodationImage.AccomodationImageModel;
import com.hosting.rest.api.services.Accomodation.AccomodationServiceImpl;
import com.hosting.rest.api.services.Accomodation.AccomodationCategory.AccomodationCategoryServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Francisco Coya Abajo
 * @version v1.0.0
 * @apiNote Controlador de ubicaciones de alojamientos.
 */
@RestController
@CrossOrigin(origins = { "*" })
@RequestMapping("/accomodations")
@Slf4j
public class AccomodationController {

	@Autowired
	private AccomodationServiceImpl accomodationService;

	@Autowired
	private AccomodationCategoryServiceImpl accomodationCategoryService;

	@PreAuthorize("hasRole('ROLE_HOST_USER') or hasRole('ROLE_ADMIN_USER')")
	@PostMapping("new")
	public AccomodationModel addNewAccomodation(@RequestBody final AccomodationModel accomodationModel) {
		return accomodationService.addNewAccomodation(accomodationModel);
	}

	@GetMapping("all")
	public Page<AccomodationModel> getAllAccomodationPaging(
			@RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) final String pageNumber,
			@RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) final String size) {
		Page<AccomodationModel> accomodations = null;

		try {
			accomodations = accomodationService.findAllAccomodations(Integer.parseInt(pageNumber),
					Integer.parseInt(size));

		} catch (NumberFormatException nfe) {
			log.error("Alguno de los valores parámetros pasados para listar los alojamientos no es un número. ", nfe);
			throw new IllegalArgumentsCustomException(
					"Alguno de los valores parámetros pasados para listar los alojamientos no es un número. ", nfe);
		}

		accomodations.getContent().get(0).getIdUserHost().getId();

		return accomodations;
	}

	@GetMapping("all/limit")
	public List<AccomodationModel> findAllAccomodations(@RequestParam(value = "max") final String maxResults) {
		List<AccomodationModel> accomodations = null;

		try {
			accomodations = accomodationService.findNAccomodations(Integer.parseInt(maxResults));

		} catch (NumberFormatException nfe) {
			log.error("El número máximo de resultados a mostrar no es válido");
			throw new IllegalArgumentsCustomException("El número máximo de resultados a mostrar no es válido");
		}
		return accomodations;
	}

	@GetMapping("all/filter")
	public List<AccomodationModel> findAccomodationByMultipleFilters(
			@RequestParam(value = "minprice") final Optional<BigDecimal> minPrice,
			@RequestParam(value = "maxprice") final Optional<BigDecimal> maxPrice,
			@RequestParam(value = "beds") final Optional<Integer> beds,
			@RequestParam(value = "bedrooms") final Optional<Integer> bedrooms,
			@RequestParam(value = "bathrooms") final Optional<Integer> bathrooms,
			@RequestParam(value = "guests") final Optional<Integer> guests) {

		return accomodationService.findAllByMultipleFilters(minPrice, maxPrice, beds, bedrooms, bathrooms, guests);
	}

	@GetMapping("{regNumber}")
	public AccomodationModel getAccomodationById(@PathVariable(value = "regNumber") final String regNumber) {
		return accomodationService.getAccomodationById(regNumber.trim());
	}

	@GetMapping("cities/{city}")
	public Page<AccomodationModel> getAccomodationsByCity(@PathVariable(value = "city") final String city,
			@RequestParam(value = "page") final String pageNumber, @RequestParam(value = "size") String size) {
		Page<AccomodationModel> accomodations = null;

		try {
			accomodations = accomodationService.findByCity(city.trim(), Integer.parseInt(size), Integer.parseInt(size));

		} catch (NumberFormatException nfe) {
			log.error("Alguno de los valores parámetros pasados para listar los alojamientos de la ciudad " + city
					+ " no es un número. ", nfe);
			throw new IllegalArgumentsCustomException(
					"Alguno de los valores parámetros pasados para listar los alojamientos de la ciudad " + city
							+ " no es un número. ",
					nfe);
		}

		return accomodations;
	}

	@GetMapping("cities/all")
	public List<String> findAllAccomodationCities() {
		return accomodationService.findAllAccomodationCities();
	}
	
	@GetMapping("city")
	public List<String> findAllByCitySearchMatch(@RequestParam(value="q") final String searchCriteria){
		return accomodationService.findByCityMatch(searchCriteria);
	}

	@GetMapping("nearby")
	public List<AccomodationModel> findNearbyAccomodations(@RequestParam(name = "lat") final BigDecimal latitude,
			@RequestParam(name = "lng") final BigDecimal longitude,
			@RequestParam(name = "distance") final Double distance) {

		return accomodationService.findByNearby(latitude, longitude, distance);
	}

	@GetMapping("category/{categoryName}")
	public List<AccomodationModel> findByCategory(@PathVariable(value = "categoryName") final String categoryToFind) {
		return accomodationService.findByCategory(categoryToFind);
	}

	@GetMapping("price")
	public List<AccomodationModel> findByPriceRange(@RequestParam(name = "minPrice") final BigDecimal minPrice,
			@RequestParam(name = "maxPrice") final BigDecimal maxPrice) {
		return accomodationService.findByPriceRange(minPrice, maxPrice);
	}

	@PreAuthorize("hasRole('ROLE_HOST_USER') or hasRole('ROLE_ADMIN_USER')")
	@PatchMapping("{regNumber}")
	public AccomodationModel updateAccomodationById(@PathVariable(value = "regNumber") final String regNumber,
			@RequestBody final AccomodationModel accomodationToUpdate) {
		return accomodationService.updateAccomodationById(regNumber, accomodationToUpdate);
	}

	@PreAuthorize("hasRole('ROLE_HOST_USER') or hasRole('ROLE_ADMIN_USER')")
	@PatchMapping("{regNumber}/category/edit")
	public void updateAccomodationCategoryByRegNumber(@PathVariable(name = "regNumber") final String regNumber,
			@Valid @RequestBody final AccomodationCategoryModel accomodationCategoryToUpdate) {
		accomodationCategoryService.updateAccomodationCategoryOfAccomodation(regNumber, accomodationCategoryToUpdate);
	}

	@PreAuthorize("hasRole('ROLE_HOST_USER') or hasRole('ROLE_ADMIN_USER')")
	@DeleteMapping("{regNumber}")
	public void removeAccomodationById(@PathVariable(value = "regNumber") final String regNumber) {
		accomodationService.removeAccomodationById(regNumber);
	}

	@GetMapping("/user/{userId}")
	public List<AccomodationModel> findByUserId(@PathVariable(name = "userId") final String userId) {
		List<AccomodationModel> accomodations = null;

		try {
			if (userId != null) {
				accomodations = accomodationService.findByUserId(Integer.parseInt(userId));
			}

		} catch (NumberFormatException nfe) {
			log.error("El id de usuario no es un número.");
			throw new IllegalArgumentsCustomException("El id de usuario no es un número.");
		}

		return accomodations;
	}

	@PreAuthorize("hasRole('ROLE_HOST_USER') or hasRole('ROLE_ADMIN_USER')")
	@DeleteMapping("/{regNumber}/images/{imageId}")
	public void deleteAccomodationImage(@PathVariable(name = "regNumber") final String regNumber,
			@PathVariable(name = "imageId") final String imageId) {
		try {
			accomodationService.removeAccomodationImage(regNumber, Integer.parseInt(imageId));

		} catch (NumberFormatException e) {
			log.error("El id de la imagen a borrar [ " + imageId + " ] no es un número.");
			throw new IllegalArgumentsCustomException("El id de la imagen a borrar no es un número");
		}
	}

	@PreAuthorize("hasRole('ROLE_BASE_USER') or hasRole('ROLE_HOST_USER') or hasRole('ROLE_ADMIN_USER')")
	@PostMapping("/{regNumber}/images/new")
	public AccomodationModel addNewImageToAccomodation(@PathVariable(name = "regNumber") final String regNumber,
			@Valid @RequestBody final AccomodationImageModel imgToAdd) {

		return accomodationService.addNewImageToExistingAccomodation(regNumber, imgToAdd);
	}

}
