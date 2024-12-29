import React, {useContext, useEffect, useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import Icon from "@mdi/react";
import {AuthContext} from "../../providers/AuthProvider";
import {mdiChevronRight} from "@mdi/js";
import {useTranslation} from "react-i18next";
import Swal from 'sweetalert2';
import '../../../styles/LogInSignIn.css';

const ConnexionUtilisateur = () => {
	
	const {SignInUser} = useContext(AuthContext);
	const {t} = useTranslation();
	const navigate = useNavigate();
	
	const [utilisateur, setUtilisateur] = useState({
		email: '',
		password: ''
	});
	
	const [errors, setErrors] = useState({
		email: '',
		password: ''
	});
	
	const handleChange = (event) => {
		const {name, value} = event.target;
		setUtilisateur({...utilisateur, [name]: value});
		setErrors({...errors, [name]: ''});
	};
	
	const handleSubmit = async (e) => {
		e.preventDefault()
		
		let validationErrors = {};
		
		if (!utilisateur.email) {
			validationErrors.email = t("connexion_page.error.email");
		}
		
		if (!utilisateur.password) {
			validationErrors.password = t("connexion_page.error.password");
		}
		
		if (Object.keys(validationErrors).length > 0) {
			setErrors(validationErrors);
			return;
		}
		
		try {
			await SignInUser(utilisateur.email, utilisateur.password)
			if (Object.keys(validationErrors).length === 0) {
				navigate("/dashboard")
			}
		} catch (error) {
			const errorMessage =
				error.message === "L'utilisateur n'est pas approuvé" ? t("connexion_page.error.not_approved") :
					error.message === "Courriel invalide" ? t("connexion_page.error.invalid_email") :
						error.message === "Mot de passe invalide" ? t("connexion_page.error.invalid_password") :
							t("connexion_page.error.unknown_error");
			
			await Swal.fire({
				icon: 'error',
				title: 'Oops...',
				text: errorMessage,
			});
		}
	}
	
	/*TODO DELETE handleTestLogin DIV ON PRODUCTION*/
	
	const handleTestLogin = async (email, password) => {
		try {
			await SignInUser(email, password);
			navigate("/dashboard");
		} catch (error) {
			await Swal.fire({
				icon: 'error',
				title: 'Oops...',
				text: t("connexion_page.error.unknown_error"),
			});
		}
	};
	
	useEffect(() => {
		// Si utilisateur change la langue pendant qu'erreur est affichée, traduire les messages d'erreur
		setErrors(prevErrors => ({
			email: prevErrors.email ? t("connexion_page.error.email") : '',
			password: prevErrors.password ? t("connexion_page.error.password") : ''
		}));
	}, [t]);
	
	return (
		<div>
			<div className="bg-auth">
				<div className="bg-auth-point"></div>
				<div className="bg-auth-point"></div>
				<div className="bg-auth-point"></div>
			</div>
			
			<div className="auth-body">
				<div className="login-frame">
					<div className="login-head">
						<div className="toolbar-items">
							<span className="mdi mdi-arrow-left"></span>
							<div className="toolbar-items">
								<Link to="/"><img src="/assets/logo/logo.svg" alt="" className={"logo"}/></Link>
							</div>
						</div>
						<h1>{t("connexion_page.connexion")}</h1>
					</div>
					<div className="login-content">
						<form onSubmit={handleSubmit}>
							<div className="input-container">
								<p>{t("connexion_page.email")}:</p>
								<input className={`${errors.email ? "field-invalid" : ""}`} type="email" name="email"
								       required value={utilisateur.email} onChange={handleChange}/>
								{errors.email &&
									<p className={"field-invalid-text"}>{errors.email}</p>}
							</div>
							<div className="input-container">
								<p>{t("connexion_page.password")}:</p>
								<input className={`${errors.password ? "field-invalid" : ""}`} type="password"
								       name="password" onChange={handleChange} value={utilisateur.password} required/>
								{errors.password &&
									<p className={"field-invalid-text"}>{errors.password}</p>}
							</div>
							
							<div style={{
								display: 'flex',
								alignItems: 'center',
								marginTop: '20px'
							}}>
								<div style={{flexGrow: 1}}></div>
								<button className="btn-filled" type="submit">
									{t("connexion_page.continue")}
									<Icon path={mdiChevronRight} size={1}/>
								</button>
							</div>
						</form>
						{/*TODO DELETE THIS DIV ON PRODUCTION*/}
						<div style={{marginTop: '20px', textAlign: 'center'}}>
							<h6 style={{textAlign: 'center'}}>Connexion Rapide!</h6>
							<div style={{
								display: 'flex',
								flexWrap: 'wrap',
								justifyContent: 'center'
							}}>
								<button style={{margin: '5px'}} onClick={() => handleTestLogin('alex@gmail.com', 'Alex123!')}>Etudiant</button>
								<button style={{margin: '5px'}} onClick={() => handleTestLogin('francois@gmail.com', 'Francois123!')}>Professeur</button>
								<button style={{margin: '5px'}} onClick={() => handleTestLogin('ubisoft@gmail.com', 'Ubisoft123!')}>Employeur</button>
								<button style={{margin: '5px'}} onClick={() => handleTestLogin('didieradmin@gmail.com', 'Didieradmin123!')}>Admin</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}

export default ConnexionUtilisateur;