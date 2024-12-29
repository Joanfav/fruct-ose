package fructose.service;

import fructose.model.Departement;
import fructose.repository.DepartementRepository;
import fructose.service.dto.DepartementDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith (MockitoExtension.class)
class DepartementServiceTest {
	
	@Mock
	private DepartementRepository departementRepository;
	
	@InjectMocks
	private DepartementService departementService;
	
	@BeforeEach
	void setUp() {
	}
	
	@Test
	void testAddDepartement_Success() {
		DepartementDTO departementDTO = new DepartementDTO();
		departementDTO.setNom("techniques_informatique");
		
		departementService.addDepartement(departementDTO);
		
		verify(departementRepository, times(1)).save(any(Departement.class));
	}
	
	@Test
	void testAddDepartement_NullDepartementDTO() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			departementService.addDepartement(null);
		});
		
		assertEquals("DepartementDTO ne peut pas être nul", exception.getMessage());
	}
	
	@Test
	void testGetDepartementByNom_Success() {
		String nom = "techniques_informatique";
		Departement departement = new Departement();
		departement.setNom("techniques_informatique");
		
		when(departementRepository.findByNom(nom)).thenReturn(List.of(departement));
		
		DepartementDTO result = departementService.getDepartementByNom(nom);
		
		assertEquals(nom, result.getNom());
		verify(departementRepository, times(1)).findByNom(nom);
	}
	
	@Test
	void testGetDepartementByNom_NotFound() {
		String nom = "NonExistant";
		when(departementRepository.findByNom(nom)).thenReturn(Collections.emptyList());
		
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			departementService.getDepartementByNom(nom);
		});
		
		assertEquals("Departement avec nom: " + nom + " n'existe pas", exception.getMessage());
	}
	
	@Test
	void testGetAllDepartements() {
		Departement departement1 = new Departement();
		Departement departement2 = new Departement();
		List<Departement> departementList = List.of(departement1, departement2);
		when(departementRepository.findAll()).thenReturn(departementList);
		
		List<Departement> result = departementService.getAllDepartements();
		
		assertEquals(2, result.size());
		verify(departementRepository, times(1)).findAll();
	}
}