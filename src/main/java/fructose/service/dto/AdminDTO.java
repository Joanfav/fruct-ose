package fructose.service.dto;

import fructose.model.Admin;
import fructose.model.enumerator.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class AdminDTO extends UtilisateurDTO {
	
	public static AdminDTO toDTO(Admin admin) {
		AdminDTO admin_dto = new AdminDTO();
		admin_dto.setId(admin.getId());
		admin_dto.setFullName(admin.getFullName());
		admin_dto.setEmail(admin.getEmail());
		admin_dto.setPassword(admin.getPassword());
		admin_dto.setMatricule(admin.getMatricule());
		admin_dto.setRole(Role.ADMIN);
		admin_dto.setDepartementDTO(DepartementDTO.toDTO(admin.getDepartement()));
		admin_dto.setCompanyName(admin.getCompanyName());
		admin_dto.setIsApproved(admin.getIsApproved());
		return admin_dto;
	}

	public static Admin toEntity(AdminDTO admin_dto) {
		Admin admin = new Admin();
		admin.setId(admin_dto.getId());
		admin.setFullName(admin_dto.getFullName());
		admin.getCredentials().setEmail(admin_dto.getEmail());
		admin.setPassword(admin_dto.getPassword());
		admin.setMatricule(admin_dto.getMatricule());
		admin.getCredentials().setRole(Role.ADMIN);
		admin.setDepartement(DepartementDTO.toEntity(admin_dto.getDepartementDTO()));
		admin.setCompanyName(admin_dto.getCompanyName());
		admin.setIsApproved(admin_dto.getIsApproved());
		return admin;
	}
}