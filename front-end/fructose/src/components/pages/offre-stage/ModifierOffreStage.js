import React, {useContext, useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {mdiAlert, mdiArrowLeft} from "@mdi/js";
import Icon from "@mdi/react";
import {AuthContext} from "../../providers/AuthProvider";
import {OffreStageContext} from "../../providers/OffreStageProvider";
import {getDepartement} from "../../../utilities/api/apiService";

const ModifierOffreStage = () => {
    const {id} = useParams();
    const {t} = useTranslation();
    const navigate = useNavigate();
    const {currentUser} = useContext(AuthContext);
    const [offreStage, setOffreStage] = useState(undefined);
    const [errors, setErrors] = useState({});
    const {fetchOffreStage, updateOffreStage} = useContext(OffreStageContext);
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        if (currentUser) {
            (async function () {
                if (currentUser.role === "EMPLOYEUR") {
                    try {
                        const response = await fetchOffreStage(id);
                        await setOffreStage(response);
                        setLoading(false)
                    } catch (error) {
                        setLoading(false)
                        console.log("error" + error);
                    }
                }
            })();
        }
    }, [currentUser, fetchOffreStage, id]);

    const handleInputChange = (event) => {
        const {name, value} = event.target;

        if (name === 'dateDebut' || name === 'dateFin' || name === 'dateLimiteCandidature') {
            setOffreStage({...offreStage, [name]: new Date(value)});
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
            dateDebut, dateFin
        } = offreStage;

        const errors = {};

        if (nom.length < 3 || nom.length > 100) {
            errors.nom = t("modifier_offre_stage_page.errors.nom");
        }
        if (poste.length < 3 || poste.length > 100) {
            errors.poste = t("modifier_offre_stage_page.errors.poste");
        }
        if (description.length < 10 || description.length > 500) {
            errors.description = t("modifier_offre_stage_page.errors.description");
        }
        if (compagnie.length < 3 || compagnie.length > 100) {
            errors.compagnie = t("modifier_offre_stage_page.errors.compagnie");
        }
        if (tauxHoraire < 0) {
            errors.tauxHoraire = t("modifier_offre_stage_page.errors.taux_horaire");
        }
        if (adresse.length < 3 || adresse.length > 100) {
            errors.adresse = t("modifier_offre_stage_page.errors.address");
        }
        if (nombreHeuresSemaine < 1) {
            errors.nombreHeuresSemaine = t("modifier_offre_stage_page.errors.nombre_heures_semaine_inferieur");
        } else if (nombreHeuresSemaine > 40) {
            errors.nombreHeuresSemaine = t("modifier_offre_stage_page.errors.nombre_heures_semaine_superieur");
        }
        if (nombrePostes < 1) {
            errors.nombrePostes = t("modifier_offre_stage_page.errors.nombre_postes");
        }
        if (departementDTO === "select" || departementDTO === "") {
            errors.departementDTO = t("modifier_offre_stage_page.errors.programme_etudes_select");
        }
        if (typeEmploi === "select" || typeEmploi === "") {
            errors.typeEmploi = t("modifier_offre_stage_page.errors.type_emploi_select");
        }
        if (modaliteTravail === "select" || modaliteTravail === "") {
            errors.modaliteTravail = t("modifier_offre_stage_page.errors.modalite_travail_select");
        }
        if (new Date(dateFin) <= new Date(dateDebut)) {
            errors.dateFin = t("modifier_offre_stage_page.errors.date_fin_before_date_debut");
        }

        return errors;
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        const errorMessage = validateFields();
        if (Object.keys(errorMessage).length > 0) {
            setErrors(errorMessage);
        } else {
            try {
                offreStage.dateDebut = new Date(offreStage.dateDebut).toISOString().split('T')[0];
                offreStage.dateFin = new Date(offreStage.dateFin).toISOString().split('T')[0];
                offreStage.dateLimiteCandidature = new Date(offreStage.dateLimiteCandidature).toISOString().split('T')[0];

                if (offreStage.departementDTO !== null && typeof offreStage.departementDTO === 'string') {
					const departement = await getDepartement(offreStage.departementDTO);
                    offreStage.departementDTO = departement;
                }



                await updateOffreStage(offreStage);

                navigate(-1);

            } catch (error) {
                console.error("Error updating offre stage:", error.message);
            }
        }
    };

    if (loading) {
        return (
            <div className="loader-container">
                <div className="loader"></div>
            </div>
        )
    } else if (offreStage == null) {
        return <h1>Erreur 404</h1>;
    }

    return (
        <>
            <div className="dashboard-card-toolbar">
                <button className="btn-icon-dashboard" onClick={() => navigate(-1)}>
                    <Icon path={mdiArrowLeft} size={1.4}/>
                </button>
                <h1>{t("modifier_offre_stage_page.title")}</h1>
            </div>
            <div className="dashboard-card">
				<br/>
                <section style={{maxWidth: "500px", margin: "auto"}}>
                    <form onSubmit={handleSubmit}>

						<h4>{t("creer_offre_stage_page.steps.1.title")}</h4>
						<p>{t("creer_offre_stage_page.steps.1.description")}</p>
                        <br/>

                        <div className={"input-container"}>
                            <p>{t("modifier_offre_stage_page.compagnie")}</p>
                            <input value={offreStage.compagnie || ''} type="text" name="compagnie"
                                   onChange={handleInputChange} required className={`${errors.compagnie ? "field-invalid" : ""}`}/>
                            <p className="field-invalid-text">{errors.compagnie}</p>
                        </div>

                        <div className={"input-container"}>
                            <p>{t("modifier_offre_stage_page.adresse")}</p>
                            <input value={offreStage.adresse || ''} type="text" name="adresse"
                                   onChange={handleInputChange}
                                   required className={`${errors.adresse ? "field-invalid" : ""}`}/>
                            <p className="field-invalid-text">{errors.adresse}</p>
                        </div>

                        <br/>

						<h4>{t("creer_offre_stage_page.steps.2.title")}</h4>
						<p>{t("creer_offre_stage_page.steps.2.description")}</p>

						<br/>

                        <div className={"input-container"}>
                            <p>{t("modifier_offre_stage_page.nom")}</p>
                            <input value={offreStage.nom || ''} type="text" name="nom" onChange={handleInputChange}
                                   required className={`${errors.nom ? "field-invalid" : ""}`}/>
                            <p className="field-invalid-text">{errors.nom}</p>
                        </div>

                        <div className={"input-container"}>
                            <p>{t("modifier_offre_stage_page.poste")}</p>
                            <input value={offreStage.poste || ''} type="text" name="poste" onChange={handleInputChange}
                                   required className={`${errors.poste ? "field-invalid" : ""}`}/>
                            <p className="field-invalid-text">{errors.poste}</p>
                        </div>

						<div className={"input-container"}>
							<p>{t("modifier_offre_stage_page.nombre_postes")}</p>
							<input value={offreStage.nombrePostes || ''} type="number" name="nombrePostes"
								   onChange={handleInputChange} required min="1" className={`${errors.nombrePostes ? "field-invalid" : ""}`}/>
							<p className="field-invalid-text">{errors.nombrePostes}</p>
						</div>

						<br/>

						<h4>{t("creer_offre_stage_page.steps.3.title")}</h4>
						<p>{t("creer_offre_stage_page.steps.3.description")}</p>

						<br/>

						<div className={"input-container"}>
							<textarea className={`${errors.description ? "field-invalid" : ""}`}
		  						value={offreStage.description} name="description" style={{height: "400px"}}
		  						onChange={handleInputChange} required></textarea>
							<p className={"field-invalid-text"}>{errors.description}</p>
						</div>

						<br/>

						<h4>{t("creer_offre_stage_page.steps.4.title")}</h4>
						<p>{t("creer_offre_stage_page.steps.4.description")}</p>
						<br/>

						<div className={"input-container"}>
						<p>{t("modifier_offre_stage_page.taux_horaire")}</p>
							<input value={offreStage.tauxHoraire !== undefined ? offreStage.tauxHoraire : ''} type="number" name="tauxHoraire"
								   onChange={handleInputChange} required min="0" className={`${errors.tauxHoraire ? "field-invalid" : ""}`}/>
							<p className="field-invalid-text">{errors.tauxHoraire}</p>
						</div>

						<div className={"input-container"}>
							<p>{t("modifier_offre_stage_page.nombre_heures_semaine")}</p>
							<input value={offreStage.nombreHeuresSemaine || ''} type="number" name="nombreHeuresSemaine"
								   onChange={handleInputChange} required min="1" className={`${errors.nombreHeuresSemaine ? "field-invalid" : ""}`}/>
							<p className="field-invalid-text">{errors.nombreHeuresSemaine}</p>
						</div>

						<div className={"input-container"}>
							<p>{t("modifier_offre_stage_page.type_emploi")}</p>
							<select name="typeEmploi" onChange={handleInputChange} value={offreStage.typeEmploi || 'select'}
									required className={`${errors.typeEmploi ? "field-invalid" : ""}`}>
								<option value="select">{t("modifier_offre_stage_page.modalites_travail.select")}</option>
								<option
									value="virtuel">{t("modifier_offre_stage_page.modalites_travail.teletravail")}</option>
								<option
									value="presentiel">{t("modifier_offre_stage_page.modalites_travail.presentiel")}</option>
								<option value="hybride">{t("modifier_offre_stage_page.modalites_travail.hybride")}</option>
							</select>
							<p className="field-invalid-text">{errors.typeEmploi}</p>
						</div>

						<div className={"input-container"}>
							<p>{t("modifier_offre_stage_page.departement")}</p>
							<select name="departementDTO" onChange={handleInputChange} className={`${errors.departementDTO ? "field-invalid" : ""}`}
									value={(offreStage.departementDTO) ? offreStage.departementDTO.nom : ''} required>
								<option value="">{t("programme.select")}</option>
								<option value="cinema">{t("programme.cinema")}</option>
								<option value="gestion_commerce">{t("programme.gestion_commerce")}</option>
								<option
									value="gestion_operations_chaine_logistique">{t("programme.gestion_operations_chaine_logistique")}</option>
								<option value="journalisme_multimedia">{t("programme.journalisme_multimedia")}</option>
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
								<option value="techniques_bureautique">{t("programme.techniques_bureautique")}</option>
								<option
									value="techniques_comptabilite_gestion">{t("programme.techniques_comptabilite_gestion")}</option>
								<option value="techniques_informatique">{t("programme.techniques_informatique")}</option>
								<option
									value="techniques_travail_social">{t("programme.techniques_travail_social")}</option>
								<option value="technologie_architecture">{t("programme.technologie_architecture")}</option>
								<option
									value="technologie_estimation_evaluation_batiment">{t("programme.technologie_estimation_evaluation_batiment")}</option>
								<option value="technologie_genie_civil">{t("programme.technologie_genie_civil")}</option>
								<option
									value="technologie_genie_electrique">{t("programme.technologie_genie_electrique")}</option>
								<option
									value="technologie_genie_physique">{t("programme.technologie_genie_physique")}</option>
								<option value="tremplin_dec">{t("programme.tremplin_dec")}</option>
							</select>
							<p className="field-invalid-text">{errors.departementDTO}</p>
						</div>

						<div className={"input-container"}>
							<p>{t("modifier_offre_stage_page.modalite_travail")}</p>
							<select name="modaliteTravail" onChange={handleInputChange} className={`${errors.modaliteTravail ? "field-invalid" : ""}`}
									value={offreStage.modaliteTravail || 'select'} required>
								<option value={"select"}>{t("modifier_offre_stage_page.types_emploi.select")}</option>
								<option
									value="temps_partiel">{t("modifier_offre_stage_page.types_emploi.temps_partiel")}</option>
								<option
									value="temps_plein">{t("modifier_offre_stage_page.types_emploi.temps_plein")}</option>
							</select>
							<p className="field-invalid-text">{errors.modaliteTravail}</p>
						</div>

						<br/>
						<h4>{t("creer_offre_stage_page.steps.5.title")}</h4>
						<p>{t("creer_offre_stage_page.steps.5.description")}</p>
						<br/>

						<div className={"input-container"}>
							<p>{t("modifier_offre_stage_page.date_limite_candidate")}</p>
							<input
								className={`${errors.dateLimiteCandidature ? "field-invalid" : ""}`}
								type="date"
								name="dateLimiteCandidature"
								onChange={handleInputChange}
								value={offreStage.dateLimiteCandidature ? new Date(offreStage.dateLimiteCandidature).toISOString().split('T')[0] : ""}
								required
								min={new Date(new Date().setDate(new Date().getDate() + 7)).toISOString().split('T')[0]}
								onKeyDown={(e) => {
									const currentDate = new Date(e.target.value);
									const minDate = new Date(new Date().setDate(new Date().getDate() + 7));

									if (e.key === 'ArrowDown' && currentDate <= minDate) {
										e.preventDefault();
									}
								}}
							/>
							<p className={"field-invalid-text"}>{errors.dateLimiteCandidature}</p>
						</div>

						<div className={"input-container"}>
							<p>{t("modifier_offre_stage_page.date_debut")}</p>
							<input
								className={`${errors.dateDebut ? "field-invalid" : ""}`}
								type="date"
								name="dateDebut"
								onChange={handleInputChange}
								value={offreStage.dateDebut ? new Date(offreStage.dateDebut).toISOString().split('T')[0] : ""}
								required
								min={offreStage.dateLimiteCandidature ? new Date(new Date(offreStage.dateLimiteCandidature).setDate(new Date(offreStage.dateLimiteCandidature).getDate() + 1)).toISOString().split('T')[0] : ""}
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
							<p>{t("modifier_offre_stage_page.date_fin")}</p>
							<input
								className={`${errors.dateFin ? "field-invalid" : ""}`}
								type="date"
								name="dateFin"
								onChange={handleInputChange}
								value={offreStage.dateFin ? new Date(offreStage.dateFin).toISOString().split('T')[0] : ""}
								required
								min={offreStage.dateDebut ? new Date(new Date(offreStage.dateDebut).setDate(new Date(offreStage.dateDebut).getDate() + 1)).toISOString().split('T')[0] : ""}
								onKeyDown={(e) => {
									const currentDate = new Date(e.target.value);
									const minDate = new Date(new Date(offreStage.dateDebut).setDate(new Date(offreStage.dateDebut).getDate() + 1));

									if (e.key === 'ArrowDown' && currentDate <= minDate) {
										e.preventDefault();
									}
								}}
							/>
							<p className={"field-invalid-text"}>{errors.dateFin}</p>
						</div>

						<br/>

                        <div className="banner bg-grey">
                            <Icon path={mdiAlert} size={1}/>
                            <p>{t("modifier_offre_stage_page.disclaimer")}</p>
                        </div>
						<br/>
                        <div className="toolbar-items">
                            <div className="toolbar-spacer"></div>
                            <button className="btn-filled"
                                    type="submit">{t("modifier_offre_stage_page.modify_offre_stage")}</button>
                        </div>
                    </form>
                </section>
                <br/>
                <br/>
                <br/>
            </div>
        </>
    );
};

export default ModifierOffreStage;