import React, {useContext, useEffect, useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {mdiArrowLeft, mdiCheckCircleOutline, mdiChevronLeft, mdiChevronRight} from "@mdi/js";
import Icon from "@mdi/react";
import {AuthContext} from "../../providers/AuthProvider";
import {getDepartement, getEmployeur} from "../../../utilities/api/apiService";

const CreerOffreStage = () => {
	const [offreStage, setOffreStage] = useState({
		nom: '',
		poste: '',
		description: '',
		compagnie: '',
		departementDTO: '',
		tauxHoraire: 0.0,
		typeEmploi: '',
		adresse: '',
		modaliteTravail: '',
		dateDebut: new Date(Date.now() + 8 * 24 * 60 * 60 * 1000),
		dateFin: new Date(Date.now() + 9 * 24 * 60 * 60 * 1000),
		nombreHeuresSemaine: 1,
		nombrePostes: 1,
		dateLimiteCandidature: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000),
		ownerDTO: null
	});
	
	const {currentToken} = useContext(AuthContext);
	const {currentUser} = useContext(AuthContext);
	const {t} = useTranslation();
	const navigate = useNavigate();
	const [errors, setErrors] = useState({});
	const [employeurs, setEmployeurs] = useState([]);
	
	const steps = 5
	const [currentStep, setCurrentStep] = useState(1);
	
	const fetchEmployeurs = async () => {
		try {
			const response = await fetch('/employeurs', {
				method: "GET",
				headers: {
					'Content-Type': 'application/json',
					'Authorization': currentToken
				},
			});
			if (response.ok) {
				return await response.json();
			}
			return [];
		} catch (error) {
			return [];
		}
	}
	
	const handleInputChange = (event) => {
		const {name, value} = event.target;
		
		if (name === 'dateDebut' || name === 'dateFin' || name === 'dateLimiteCandidature') {
			setOffreStage({...offreStage, [name]: new Date(value)});
		} else if (name === 'employeur') {
			const selectedEmployeur = employeurs.find(employeur => employeur.id === Number(value));
			setOffreStage({...offreStage, ownerDTO: selectedEmployeur});
		} else {
			setOffreStage({...offreStage, [name]: value});
		}
	}
	
	const validateFields = () => {
		const {
			nom, poste, description,
			compagnie, departementDTO, tauxHoraire,
			typeEmploi, adresse, modaliteTravail,
			nombreHeuresSemaine, nombrePostes,
		} = offreStage;
		
		const errors = {};
		
		if (currentStep === 1 || currentStep === 5) {
			if (compagnie.length < 3 || compagnie.length > 100) {
				errors.compagnie = t("creer_offre_stage_page.errors.compagnie");
			}
			if (adresse.length < 3 || adresse.length > 100) {
				errors.adresse = t("creer_offre_stage_page.errors.address");
			}
		}
		if (currentStep === 2 || currentStep === 5) {
			if (nom.length < 3 || nom.length > 100) {
				errors.nom = t("creer_offre_stage_page.errors.nom");
			}
			if (poste.length < 3 || poste.length > 100) {
				errors.poste = t("creer_offre_stage_page.errors.poste");
			}
			if (nombrePostes < 1) {
				errors.nombrePostes = t("creer_offre_stage_page.errors.nombre_postes");
			}
		}
		if (currentStep === 3 || currentStep === 5) {
			if (description.length < 10 || description.length > 500) {
				errors.description = t("creer_offre_stage_page.errors.description");
			}
		}
		if (currentStep === 4 || currentStep === 5) {
			if (tauxHoraire < 0) {
				errors.tauxHoraire = t("creer_offre_stage_page.errors.taux_horaire");
			}
			if (nombreHeuresSemaine < 1) {
				errors.nombreHeuresSemaine = t("creer_offre_stage_page.errors.nombre_heures_semaine_inferieur");
			} else if (nombreHeuresSemaine > 40) {
				errors.nombreHeuresSemaine = t("creer_offre_stage_page.errors.nombre_heures_semaine_superieur");
			}
		}
		if (currentStep === 5) {
			if (departementDTO === "select" || departementDTO === "") {
				errors.departementDTO = t("creer_offre_stage_page.errors.programme_etudes_select");
			}
			if (typeEmploi === "select" || typeEmploi === "") {
				errors.typeEmploi = t("creer_offre_stage_page.errors.type_emploi_select");
			}
			if (modaliteTravail === "select" || modaliteTravail === "") {
				errors.modaliteTravail = t("creer_offre_stage_page.errors.modalite_travail_select");
			}
		}
		
		return errors;
	}
	
	const getTranslatedErrors = (prevErrors) => {
		const updatedErrors = {...prevErrors};
		
		if (prevErrors.nom) updatedErrors.nom = t("creer_offre_stage_page.errors.nom");
		if (prevErrors.poste) updatedErrors.poste = t("creer_offre_stage_page.errors.poste");
		if (prevErrors.description) updatedErrors.description = t("creer_offre_stage_page.errors.description");
		if (prevErrors.compagnie) updatedErrors.compagnie = t("creer_offre_stage_page.errors.compagnie");
		if (prevErrors.tauxHoraire) updatedErrors.tauxHoraire = t("creer_offre_stage_page.errors.taux_horaire");
		if (prevErrors.adresse) updatedErrors.adresse = t("creer_offre_stage_page.errors.address");
		if (prevErrors.nombreHeuresSemaine) updatedErrors.nombreHeuresSemaine = t("creer_offre_stage_page.errors.nombre_heures_semaine_inferieur");
		if (prevErrors.nombrePostes) updatedErrors.nombrePostes = t("creer_offre_stage_page.errors.nombre_postes");
		if (prevErrors.departementDTO) updatedErrors.departementDTO = t("creer_offre_stage_page.errors.programme_etudes_select");
		if (prevErrors.typeEmploi) updatedErrors.typeEmploi = t("creer_offre_stage_page.errors.type_emploi_select");
		if (prevErrors.modaliteTravail) updatedErrors.modaliteTravail = t("creer_offre_stage_page.errors.modalite_travail_select");
		
		return updatedErrors;
	};

// Call `getTranslatedErrors` directly where you need to update errors, instead of inside `useEffect`.
	const updateErrors = () => {
		setErrors((prevErrors) => {
			const translatedErrors = getTranslatedErrors(prevErrors);
			// Only update if errors actually change to avoid re-renders
			return JSON.stringify(prevErrors) !== JSON.stringify(translatedErrors) ? translatedErrors : prevErrors;
		});
	};
	
	useEffect(() => {
		updateErrors();
	}, [t]);
	
	useEffect(() => {
		const fetchData = async () => {
			if (currentUser && currentUser.role === 'ADMIN') {
				await fetchEmployeurs().then((employeurs) => {
					setEmployeurs(employeurs);
				});
			}
		}
		fetchData();
	}, [currentUser]);
	
	const handleSubmit = async (event) => {
		event.preventDefault();
		const errorMessage = validateFields();
		if (Object.keys(errorMessage).length > 0) {
			setErrors(errorMessage);
		} else {
			if (currentStep >= 5) {
				offreStage.dateDebut = new Date(offreStage.dateDebut).toISOString().split('T')[0];
				offreStage.dateFin = new Date(offreStage.dateFin).toISOString().split('T')[0];
				offreStage.dateLimiteCandidature = new Date(offreStage.dateLimiteCandidature).toISOString().split('T')[0];
				const departement = await getDepartement(offreStage.departementDTO);
				if (departement) {
					offreStage.departementDTO = departement;
				}

				if(offreStage.ownerDTO){
					const employeur = await getEmployeur(offreStage.ownerDTO.id, currentToken);
					if (employeur) {
						offreStage.ownerDTO = employeur;
					}
				}

				fetch('/creer-offre-stage', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
						'Authorization': currentToken
					},
					body: JSON.stringify(offreStage)
				})
					.then(response => {
						if (!response.ok) {
							return response.text().then(text => {
								throw new Error(text)
							});
						}
						return response;
					}).then(() => {
					setCurrentStep(6)
				}).catch(error => {
					setErrors('Erreur :' + error.message);
					console.error('Erreur:', error);
				});
			} else {
				setCurrentStep(currentStep + 1)
			}
		}
	};
	
	const getStep = () => {
		switch (currentStep) {
			case 1:
				return (
					<div className="stepper-form-step">
						<h4>{t("creer_offre_stage_page.steps.1.title")}</h4>
						<p>{t("creer_offre_stage_page.steps.1.description")}</p>
						<br/>

						<div className={"input-container"}>
							<p>{t("creer_offre_stage_page.compagnie")}</p>
							<input className={`${errors.compagnie ? "field-invalid" : ""}`}
							       value={offreStage.compagnie}
							       type="text" name="compagnie" onChange={handleInputChange} required/>
							<p className={"field-invalid-text"}>{errors.compagnie}</p>
						</div>
						
						<div className={"input-container"}>
							<p>{t("creer_offre_stage_page.address")}</p>
							<input className={`${errors.adresse ? "field-invalid" : ""}`} value={offreStage.adresse}
							       type="text" name="adresse" onChange={handleInputChange} required/>
							<p className={"field-invalid-text"}>{errors.adresse}</p>
						</div>
					</div>
				);
			case 2:
				return (
					<div className="stepper-form-step">
						<h4>{t("creer_offre_stage_page.steps.2.title")}</h4>
						<p>{t("creer_offre_stage_page.steps.2.description")}</p>

						<br/>
						
						<div className={"input-container"}>
							<p>{t("creer_offre_stage_page.nom")}</p>
							<input className={`${errors.nom ? "field-invalid" : ""}`} value={offreStage.nom}
							       type="text"
							       name="nom" onChange={handleInputChange} required/>
							<p className={"field-invalid-text"}>{errors.nom}</p>
						</div>
						
						<div className={"input-container"}>
							<p>{t("creer_offre_stage_page.poste")}</p>
							<input className={`${errors.poste ? "field-invalid" : ""}`} value={offreStage.poste}
							       type="text" name="poste" onChange={handleInputChange} required/>
							<p className={"field-invalid-text"}>{errors.poste}</p>
							<p className="text-mini text-dark">Exemples: Technicien, Consultant, Scrum Master</p>
						</div>
						
						<div className={"input-container"}>
							<p>{t("creer_offre_stage_page.nombre_postes")}</p>
							<input className={`${errors.nombrePostes ? "field-invalid" : ""}`}
							       value={offreStage.nombrePostes} type="number" name="nombrePostes"
							       onChange={handleInputChange} required min="1"/>
							<p className={"field-invalid-text"}>{errors.nombrePostes}</p>
						</div>
						
						<div className={"input-container"}>
							{currentUser && currentUser.role === 'ADMIN' &&
								<>
									<p>{t("creer_offre_stage_page.employeurs")}</p>
									<select name="employeur" onChange={handleInputChange}
									        value={offreStage.ownerDTO ? offreStage.ownerDTO.id : ''}
									        required>
										<option value="">{t("creer_offre_stage_page.employeur_select")}</option>
										{employeurs.map((employeur) => (
											<option key={employeur.id}
											        value={employeur.id}>{employeur.fullName} - {employeur.companyName}</option>
										))}
									</select>
								</>
							}
						</div>
					</div>
				)
			case 3:
				return (
					<div className="stepper-form-step">

						<h4>{t("creer_offre_stage_page.steps.3.title")}</h4>
						<p>{t("creer_offre_stage_page.steps.3.description")}</p>

						<div className={"input-container"}>
                            <textarea className={`${errors.description ? "field-invalid" : ""}`}
                                      value={offreStage.description} name="description" style={{height: "200px"}}
                                      onChange={handleInputChange} required></textarea>
							<p className={"field-invalid-text"}>{errors.description}</p>
						</div>
					</div>
				)
			case 4:
				return (
					<div className="stepper-form-step">
						<h4>{t("creer_offre_stage_page.steps.4.title")}</h4>
						<p>{t("creer_offre_stage_page.steps.4.description")}</p>
						<br/>
						
						<div className={"input-container"}>
							<p>{t("creer_offre_stage_page.taux_horaire")}</p>
							<input className={`${errors.tauxHoraire ? "field-invalid" : ""}`}
							       value={offreStage.tauxHoraire}
							       type="number" name="tauxHoraire" onChange={handleInputChange} required min="0"/>
							<p className={"field-invalid-text"}>{errors.tauxHoraire}</p>
						</div>
						
						<div className={"input-container"}>
							<p>{t("creer_offre_stage_page.nombre_heures_semaine")}</p>
							<input className={`${errors.nombreHeuresSemaine ? "field-invalid" : ""}`}
							       value={offreStage.nombreHeuresSemaine} type="number" name="nombreHeuresSemaine"
							       onChange={handleInputChange} required min="1"/>
							<p className={"field-invalid-text"}>{errors.nombreHeuresSemaine}</p>
						</div>
						
						<div className={"input-container"}>
							<p>{t("creer_offre_stage_page.type_emploi")}</p>
							<select name="typeEmploi" onChange={handleInputChange} value={offreStage.typeEmploi}
							        required>
								<option
									value="">{t("creer_offre_stage_page.modalites_travail.select")}</option>
								<option
									value="virtuel">{t("creer_offre_stage_page.modalites_travail.virtuel")}</option>
								<option
									value="presentiel">{t("creer_offre_stage_page.modalites_travail.presentiel")}</option>
								<option
									value="hybride">{t("creer_offre_stage_page.modalites_travail.hybride")}</option>
							</select>
							<p className={"field-invalid-text"}>{errors.typeEmploi}</p>
						</div>
						
						<div className={"input-container"}>
							<p>{t("creer_offre_stage_page.programme_etudes")}</p>
							<select name="departementDTO" onChange={handleInputChange}
							        value={offreStage.departementDTO}
							        required>
								<option value="">{t("programme.select")}</option>
								<option value="cinema">{t("programme.cinema")}</option>
								<option value="gestion_commerce">{t("programme.gestion_commerce")}</option>
								<option
									value="gestion_operations_chaine_logistique">{t("programme.gestion_operations_chaine_logistique")}</option>
								<option
									value="journalisme_multimedia">{t("programme.journalisme_multimedia")}</option>
								<option
									value="langues_trilinguisme_cultures">{t("programme.langues_trilinguisme_cultures")}</option>
								<option
									value="photographie_design_graphique">{t("programme.photographie_design_graphique")}</option>
								<option value="sciences_nature">{t("programme.sciences_nature")}</option>
								<option
									value="sciences_humaines_administration_economie">{t("programme.sciences_humaines_administration_economie")}</option>
								<option
									value="sciences_humaines_individu_relations_humaines">{t("programme.sciences_humaines_individu_relations_humaines")}</option>
								<option
									value="sciences_humaines_monde_en_action">{t("programme.sciences_humaines_monde_en_action")}</option>
								<option value="soins_infirmiers">{t("programme.soins_infirmiers")}</option>
								<option
									value="soins_infirmiers_auxiliaires">{t("programme.soins_infirmiers_auxiliaires")}</option>
								<option
									value="techniques_education_enfance">{t("programme.techniques_education_enfance")}</option>
								<option
									value="techniques_bureautique">{t("programme.techniques_bureautique")}</option>
								<option
									value="techniques_comptabilite_gestion">{t("programme.techniques_comptabilite_gestion")}</option>
								<option
									value="techniques_informatique">{t("programme.techniques_informatique")}</option>
								<option
									value="techniques_travail_social">{t("programme.techniques_travail_social")}</option>
								<option
									value="technologie_architecture">{t("programme.technologie_architecture")}</option>
								<option
									value="technologie_estimation_evaluation_batiment">{t("programme.technologie_estimation_evaluation_batiment")}</option>
								<option
									value="technologie_genie_civil">{t("programme.technologie_genie_civil")}</option>
								<option
									value="technologie_genie_electrique">{t("programme.technologie_genie_electrique")}</option>
								<option
									value="technologie_genie_physique">{t("programme.technologie_genie_physique")}</option>
								<option value="tremplin_dec">{t("programme.tremplin_dec")}</option>
							</select>
							<p className={"field-invalid-text"}>{errors.departementDTO}</p>
						</div>
						<div className={"input-container"}>
							<p>{t("creer_offre_stage_page.modalite_travail")}</p>
							<select name="modaliteTravail" onChange={handleInputChange}
							        value={offreStage.modaliteTravail}
							        required>
								<option value={"select"}>{t("creer_offre_stage_page.types_emploi.select")}</option>
								<option
									value="temps_partiel">{t("creer_offre_stage_page.types_emploi.temps_partiel")}</option>
								<option
									value="temps_plein">{t("creer_offre_stage_page.types_emploi.temps_plein")}</option>
							</select>
							<p className={"field-invalid-text"}>{errors.modaliteTravail}</p>
						</div>
					</div>
				)
			case 5:
				return (
					<div className="stepper-form-step">
						<h4>{t("creer_offre_stage_page.steps.5.title")}</h4>
						<p>{t("creer_offre_stage_page.steps.5.description")}</p>
						<br/>
						<div className={"input-container"}>
							<div className={"input-container"}>
								<p>{t("creer_offre_stage_page.date_limite_candidate")}</p>
								<input
									className={`${errors.dateLimiteCandidature ? "field-invalid" : ""}`}
									type="date"
									name="dateLimiteCandidature"
									onChange={handleInputChange}
									value={offreStage.dateLimiteCandidature instanceof Date && !isNaN(offreStage.dateLimiteCandidature.getTime())
										? offreStage.dateLimiteCandidature.toISOString().split('T')[0]
										: ""}
									required
									min={new Date(new Date().setDate(new Date().getDate() + 7)).toISOString().split('T')[0]}
									onKeyDown={(e) => {
										const currentDate = new Date(e.target.value);
										const minDate = new Date(new Date().setDate(new Date().getDate() + 7));  // 7 days from today

										if (e.key === 'ArrowDown' && currentDate <= minDate) {
											e.preventDefault(); // Prevent going below 7 days from today
										}
									}}
								/>
								<p className={"field-invalid-text"}>{errors.dateLimiteCandidature}</p>
							</div>

							<p>{t("creer_offre_stage_page.date_debut")}</p>
							<input
								className={`${errors.dateDebut ? "field-invalid" : ""}`}
								type="date"
								name="dateDebut"
								onChange={handleInputChange}
								value={offreStage.dateDebut instanceof Date && !isNaN(offreStage.dateDebut.getTime())
									? offreStage.dateDebut.toISOString().split('T')[0]
									: ""}
								required
								min={offreStage.dateLimiteCandidature instanceof Date && !isNaN(offreStage.dateLimiteCandidature.getTime())
									? new Date(new Date(offreStage.dateLimiteCandidature).setDate(new Date(offreStage.dateLimiteCandidature).getDate() + 1)).toISOString().split('T')[0]
									: ""}
								onKeyDown={(e) => {
									const minDate = new Date(new Date(offreStage.dateLimiteCandidature).setDate(new Date(offreStage.dateLimiteCandidature).getDate() + 1));

									if (e.key === 'ArrowDown' && offreStage.dateDebut.getTime() <= minDate.getTime()) {
										e.preventDefault(); // Prevent going below dateLimiteCandidature + 1 day
									}
								}}
							/>
							<p className={"field-invalid-text"}>{errors.dateDebut}</p>
						</div>

						<div className={"input-container"}>
							<p>{t("creer_offre_stage_page.date_fin")}</p>
							<input
								className={`${errors.dateFin ? "field-invalid" : ""}`}
								type="date"
								name="dateFin"
								onChange={handleInputChange}
								value={offreStage.dateFin instanceof Date && !isNaN(offreStage.dateFin.getTime())
									? offreStage.dateFin.toISOString().split('T')[0]
									: ""}
								required
								min={offreStage.dateDebut instanceof Date && !isNaN(offreStage.dateDebut.getTime())
									? new Date(new Date(offreStage.dateDebut).setDate(new Date(offreStage.dateDebut).getDate() + 1)).toISOString().split('T')[0]
									: ""}
								onKeyDown={(e) => {
									const currentDate = new Date(e.target.value);
									const minDate = new Date(new Date(offreStage.dateDebut).setDate(new Date(offreStage.dateDebut).getDate() + 1));

									if (e.key === 'ArrowDown' && currentDate <= minDate) {
										e.preventDefault(); // Prevent going below dateDebut + 1 day
									}
								}}
							/>
							<p className={"field-invalid-text"}>{errors.dateFin}</p>
						</div>
					</div>
				)
			case 6:
				return (
					<div className="dashboard-placeholder-card" style={{
						backgroundColor: "transparent",
						height: "100%"
					}}>
						<div className="loader-container">
							<div>
								<Icon path={mdiCheckCircleOutline} size={2.5}/>
								<h3>{t("creer_offre_stage_page.steps.6.success")}</h3>
								<p>{t("creer_offre_stage_page.steps.6.success_text")}</p>
								<br/>
								<button className="btn-filled" type="button"
										onClick={() => navigate('/dashboard')}>{t("creer_offre_stage_page.steps.6.go_to_dashboard")}</button>
							</div>
						</div>
					</div>
				)
		}
	}

	const getStepClass = (step) => {
		if (currentStep === step) {
			return "active"
		} else if (currentStep > step) {
			return "completed"
		}
		return ""
	}

	const returnToStep = () => {
		if (currentStep - 1 > 0) {
			setCurrentStep(currentStep - 1)
		}
	}
	
	
	return (
		<>
			<div className="dashboard-card-toolbar">
				<Link to="/dashboard">
					<button className="btn-icon-dashboard"><Icon path={mdiArrowLeft} size={1.4}/>
					</button>
				</Link>
				<h1>{t("creer_offre_stage_page.title")}</h1>
			</div>
			<div className="dashboard-card" style={{
				display: "flex",
				height: "calc(100vh - 300px)",
				minHeight: "600px"
			}}>
				<div style={{
					width: "100%",
					maxWidth: "300px",
					backgroundColor: "#21277c",
					borderRadius: "5px 0 0 5px",
					padding: "20px",
					color: "white"
				}}>
					<div className="vertical-stepper">
						<div className={"vertical-stepper-item " + getStepClass(1)}>
							<div className="vertical-stepper-content">
								<h6 className="vertical-stepper-title">Informations de base</h6>
								<p className="vertical-stepper-desc">Informations cruciales pour le profil de votre compagnie</p>
							</div>
						</div>
						<div className={"vertical-stepper-item " + getStepClass(2)}>
							<div className="vertical-stepper-content">
								<h6 className="vertical-stepper-title">Informations sur l'emploi</h6>
								<p className="vertical-stepper-desc">Impression et la visibilité du public</p>
							</div>
						</div>
						<div className={"vertical-stepper-item " + getStepClass(3)}>
							<div className="vertical-stepper-content">
								<h6 className="vertical-stepper-title">Description</h6>
								<p className="vertical-stepper-desc">Description sur l'emploi</p>
							</div>
						</div>
						<div className={"vertical-stepper-item " + getStepClass(4)}>
							<div className="vertical-stepper-content">
								<h6 className="vertical-stepper-title">Particularités</h6>
								<p className="vertical-stepper-desc">Horaire et Taux horaire du salarié</p>
							</div>
						</div>
						<div className={"vertical-stepper-item " + getStepClass(5)}>
							<div className="vertical-stepper-content">
								<h6 className="vertical-stepper-title">Dates</h6>
								<p className="vertical-stepper-desc">Périodes clés liés au poste</p>
							</div>
						</div>
						<div className={"vertical-stepper-item " + getStepClass(6)}>
							<div className="vertical-stepper-content">
								<h6 className="vertical-stepper-title">Soumission</h6>
							</div>
						</div>
						<div className="vertical-stepper-item">
							<div className="vertical-stepper-content">
								<h6 className="vertical-stepper-title">Approbation</h6>
							</div>
						</div>
					</div>
				</div>
				<div style={{position: "relative", flexGrow: "1"}}>
					<form onSubmit={handleSubmit}>
						{
							getStep()
						}
						{
							(currentStep < 6) ?
								<div className="form-dock">
									{
										(currentStep !== 1) ?
											<button type="button" onClick={() => {
												returnToStep()
											}}>
												<Icon path={mdiChevronLeft} size={1}/>
											</button>
											: null
									}
									<div className={"toolbar-spacer"}></div>
									<button type="submit" className={"btn-filled"}>{t("information_base_page.continue")}<Icon path={mdiChevronRight} size={1}/>
									</button>
								</div> : null
						}
					</form>
				</div>
			</div>
		</>
	);
}
export default CreerOffreStage;