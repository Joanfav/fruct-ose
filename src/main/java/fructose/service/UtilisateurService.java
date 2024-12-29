package fructose.service;

import fructose.model.*;
import fructose.model.enumerator.Role;
import fructose.repository.*;
import fructose.security.JwtTokenProvider;
import fructose.security.exception.InvalidJwtTokenException;
import fructose.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilisateurService {
	
	private final EtudiantRepository etudiantRepository;
	private final ProfesseurRepository professeurRepository;
	private final EmployeurRepository employeurRepository;
	private final AdminRepository adminRepository;
	
	private final PasswordEncoder passwordEncoder;
	private final UtilisateurRepository utilisateurRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthenticationManager authenticationManager;
	
	public boolean isValidRole(String role) {
		return Arrays.stream(Role.values()).anyMatch((t) -> t.name().equals(role));
	}
	
	public UtilisateurDTO getUtilisateurById(Long id, Role role) {
		switch (role) {
			case ETUDIANT:
				Etudiant etudiant = etudiantRepository.findById(id).orElse(null);
				if (etudiant == null) {
					throw new IllegalArgumentException("Etudiant avec ID: " + id + " n'existe pas");
				}
				return EtudiantDTO.toDTO(etudiant);
			case PROFESSEUR:
				Professeur professeur = professeurRepository.findById(id).orElse(null);
				if (professeur == null) {
					throw new IllegalArgumentException("Professeur avec ID: " + id + " n'existe pas");
				}
				return ProfesseurDTO.toDTO(professeur);
			case EMPLOYEUR:
				Employeur employeur = employeurRepository.findById(id).orElse(null);
				if (employeur == null) {
					throw new IllegalArgumentException("Employeur avec ID: " + id + " n'existe pas");
				}
				return EmployeurDTO.toDTO(employeur);
			case ADMIN:
				Admin admin = adminRepository.findById(id).orElse(null);
				if (admin == null) {
					throw new IllegalArgumentException("Admin avec ID: " + id + " n'existe pas");
				}
				return AdminDTO.toDTO(admin);
			default:
				throw new IllegalArgumentException("Type d'utilisateur : " + role + " n'est pas valide");
		}
	}
	
	public void addUtilisateur(UtilisateurDTO utilisateurDTO) {
		Utilisateur utilisateur = UtilisateurDTO.toEntity(utilisateurDTO);
		utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
		saveUtilisateur(utilisateur);
	}
	
	public void updateUtilisateur(UtilisateurDTO utilisateurDTO, Role role) {
		Utilisateur utilisateur = UtilisateurDTO.toEntity(utilisateurDTO);
		utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
		saveUtilisateur(utilisateur);
	}
	
	private void saveUtilisateur(Utilisateur utilisateur) {
		switch (utilisateur.getRole()) {
			case ETUDIANT -> etudiantRepository.save((Etudiant) utilisateur);
			case PROFESSEUR -> professeurRepository.save((Professeur) utilisateur);
			case EMPLOYEUR -> employeurRepository.save((Employeur) utilisateur);
			case ADMIN -> adminRepository.save((Admin) utilisateur);
		}
	}
	
	public void deleteUtilisateurByID(Long id, Role role) {
		String saveFullName = getUtilisateurById(id, role).getFullName();
		switch (role) {
			case ETUDIANT -> etudiantRepository.deleteById(id);
			case PROFESSEUR -> professeurRepository.deleteById(id);
			case EMPLOYEUR -> employeurRepository.deleteById(id);
			case ADMIN -> adminRepository.deleteById(id);
		}
	}
	
	public List<UtilisateurDTO> getUtilisateurs(Role role) {
		return switch (role) {
			case ETUDIANT -> etudiantRepository.findAll()
					.stream()
					.map(EtudiantDTO::toDTO)
					.collect(Collectors.toList());
			case PROFESSEUR -> professeurRepository.findAll()
					.stream()
					.map(ProfesseurDTO::toDTO)
					.collect(Collectors.toList());
			case EMPLOYEUR -> employeurRepository.findAll()
					.stream()
					.map(EmployeurDTO::toDTO)
					.collect(Collectors.toList());
			case ADMIN -> adminRepository.findAll()
					.stream()
					.map(AdminDTO::toDTO)
					.collect(Collectors.toList());
		};
	}
	
	public boolean isEmailTaken(String email) {
		return utilisateurRepository.findByEmail(email) != null;
	}
	
	public boolean isMatriculeTaken(String matricule) {
		return utilisateurRepository.findByMatricule(matricule) != null;
	}
	
	public String authenticateUser(String email, String password) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		Utilisateur utilisateur = utilisateurRepository.findByEmail(email);
		
		if (!utilisateur.getIsApproved()) {
			throw new IllegalArgumentException("L'utilisateur n'est pas approuvé");
		}
		return jwtTokenProvider.generateToken(authentication);
	}
	
	public UtilisateurDTO getUtilisateurByToken(String token) {
		token = token.startsWith("Bearer") ? token.substring(7) : token;
		String email = jwtTokenProvider.getEmailFromJWT(token);
		Utilisateur user = utilisateurRepository.findByEmail(email);//.orElseThrow(UserNotFoundException::new);
		return switch (user.getRole()) {
			case ETUDIANT -> etudiantRepository.findById(user.getId()).map(EtudiantDTO::toDTO).orElse(null);
			case EMPLOYEUR -> employeurRepository.findById(user.getId()).map(EmployeurDTO::toDTO).orElse(null);
			case PROFESSEUR -> professeurRepository.findById(user.getId()).map(ProfesseurDTO::toDTO).orElse(null);
			case ADMIN -> adminRepository.findById(user.getId()).map(AdminDTO::toDTO).orElse(null);
		};
	}
	
	public boolean validationToken(String token) {
		try {
			jwtTokenProvider.validateToken(token);
			return true;
		} catch (InvalidJwtTokenException ex) {
			return false;
		}
	}
	
	public UtilisateurDTO getUtilisateurByEmail(String email) {
		Utilisateur utilisateur = utilisateurRepository.findByEmail(email);
		if (utilisateur == null) {
			throw new IllegalArgumentException("L'utilisateur avec email " + email + " n'existe pas");
		}
		return UtilisateurDTO.toDTO(utilisateur);
	}
	
	public Role getRoleById(Long id) {
		if (etudiantRepository.existsById(id)) {
			return Role.ETUDIANT;
		} else if (professeurRepository.existsById(id)) {
			return Role.PROFESSEUR;
		} else if (employeurRepository.existsById(id)) {
			return Role.EMPLOYEUR;
		} else if (adminRepository.existsById(id)) {
			return Role.ADMIN;
		} else {
			throw new IllegalArgumentException("L'utilisateur avec ID " + id + " n'existe pas");
		}
	}
	
	public List<UtilisateurDTO> getNonApprovedUsers() {
		// NE TOUCHE PAS À CETTE MÉTHODE. IGNORE INDICES D'INTELIJ. ÇA VA CASSER LE CODE
		List<Utilisateur> nonApprovedUsers = utilisateurRepository.findByIsApproved(false);
		return nonApprovedUsers.stream().map(UtilisateurDTO::toDTO).toList();
	}
	
	public void approveUser(Long id) {
		Role role = getRoleById(id);
		switch (role) {
			case ETUDIANT -> {
				Etudiant etudiant = etudiantRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Etudiant avec ID: " + id + " n'existe pas"));
				etudiant.setIsApproved(true);
				etudiantRepository.save(etudiant);
			}
			case PROFESSEUR -> {
				Professeur professeur = professeurRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Professeur avec ID: " + id + " n'existe pas"));
				professeur.setIsApproved(true);
				professeurRepository.save(professeur);
			}
			case EMPLOYEUR -> {
				Employeur employeur = employeurRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employeur avec ID: " + id + " n'existe pas"));
				employeur.setIsApproved(true);
				employeurRepository.save(employeur);
			}
			case ADMIN -> {
				Admin admin = adminRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Admin avec ID: " + id + " n'existe pas"));
				admin.setIsApproved(true);
				adminRepository.save(admin);
			}
		}
	}

	public List<UtilisateurDTO> getEmployeursApproved() {
		List<Utilisateur> employeurs = employeurRepository.findByIsApproved(true)
				.stream()
				.filter(utilisateur -> utilisateur.getRole() == Role.EMPLOYEUR)
				.toList();
		return employeurs.stream().map(EmployeurDTO::toDTO).toList();
	}
	
	public boolean verifyRoleEligibilityByToken(String token, Role[] roles) {
		UtilisateurDTO utilisateur = getUtilisateurByToken(token);
		return Arrays.stream(Role.values()).anyMatch((t) -> t.name().equals(utilisateur.getRole()));
	}
	
	public boolean verifyRoleEligibilityByToken(String token, Role role) {
		UtilisateurDTO utilisateur = getUtilisateurByToken(token);
		return utilisateur.getRole().equals(role);
	}

	public UtilisateurDTO getEmployeurById(Long id) {
		Employeur employeur = employeurRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employeur avec ID: " + id + " n'existe pas"));
		return EmployeurDTO.toDTO(employeur);
	}
}