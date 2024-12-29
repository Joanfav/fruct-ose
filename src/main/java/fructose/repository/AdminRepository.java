package fructose.repository;

import fructose.model.Admin;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends UtilisateurRepository<Admin, Long> {

}