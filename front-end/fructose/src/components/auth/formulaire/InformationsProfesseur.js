import React, {useEffect, useState} from "react";
import Icon from "@mdi/react";
import {mdiChevronLeft, mdiChevronRight} from "@mdi/js";
import {useTranslation} from "react-i18next";
import {getDepartement, isMatriculeTaken} from '../../../utilities/api/apiService';
import Swal from 'sweetalert2';

const InformationsProfesseur = ({utilisateur, handleChange, switchStep}) => {

    const {t} = useTranslation();
    const [errors, setErrors] = useState({});
    const [formSubmitted, setFormSubmitted] = useState(false);

    const handleSubmit = async (event) => {
        event.preventDefault();
        setFormSubmitted(true);
        const errorMessage = validateFields(utilisateur);
        const matriculeTaken = await isMatriculeTaken(utilisateur.matricule);
        if (matriculeTaken) {
            errorMessage.matricule = t("information_professeur_page.error.employee_number_taken");
            Swal.fire({
                title: t("information_professeur_page.error.error_title"),
                text: t("information_professeur_page.error.employee_number_taken"),
                icon: 'error',
                confirmButtonText: 'OK'
            });
        }
        if (Object.keys(errorMessage).length > 0) {
            setErrors(errorMessage);
        } else {
            const departement = await getDepartement(utilisateur.departement);
            if (departement) {
                utilisateur.departementDTO = departement;
            }
            switchStep(true);
        }
    };

    const handleInputChange = (event) => {
        const {name} = event.target;
        handleChange(event);
        setErrors((prevErrors) => ({...prevErrors, [name]: ""}));
    };



    const validateFields = (user = {}) => {
        let errors = {};

        if (user.matricule && !/^\d{7}$/.test(user.matricule)) {
            errors.matricule = t("information_professeur_page.error.employee_number");
        }

        if (user.departement && user.departement.length === 0) {
            errors.departement = t("information_professeur_page.error.department");
        }

        return errors;
    };

    useEffect(() => {
        if (formSubmitted) {
            setErrors((prevErrors) => {
                const updatedErrors = {...prevErrors};

                if (utilisateur.matricule && !/^\d{7}$/.test(utilisateur.matricule)) {
                    updatedErrors.matricule = t("information_professeur_page.error.employee_number");
                } else {
                    delete updatedErrors.matricule;
                }

                if (utilisateur.departement && utilisateur.departement.length === 0) {
                    updatedErrors.departement = t("information_professeur_page.error.department");
                } else {
                    delete updatedErrors.departement;
                }

                return updatedErrors;
            });
        }
    }, [utilisateur.matricule, utilisateur.departement, t, formSubmitted]);

    return (
        <div className={"form-signup-condensed"}>
            <h4>{t("information_professeur_page.title")}</h4>
            <p>{t("information_professeur_page.description")}</p>
            <br/>
            <form onSubmit={handleSubmit}>
                <div className={"input-container"}>
                    <p>{t("information_professeur_page.employee_number")}:</p>
                    <input type="text" name="matricule" className={`${errors.matricule ? "field-invalid" : ""}`} required value={utilisateur.matricule || ""} onChange={handleInputChange}/>
                    {formSubmitted && <p className={"field-invalid-text"}>{errors.matricule}</p>}
                </div>
                <div className={"input-container"}>
                    <p>{t("information_professeur_page.department")}:</p>
                    <select name="departement" className={`${errors.departement ? "field-invalid" : ""}`} onChange={handleInputChange} value={utilisateur.departement || ""} required>
                        <option value="">{t("programme.select")}</option>
                        <option value="cinema">{t("programme.cinema")}</option>
                        <option value="gestion_commerce">{t("programme.gestion_commerce")}</option>
                        <option value="gestion_operations_chaine_logistique">{t("programme.gestion_operations_chaine_logistique")}</option>
                        <option value="journalisme_multimedia">{t("programme.journalisme_multimedia")}</option>
                        <option value="langues_trilinguisme_cultures">{t("programme.langues_trilinguisme_cultures")}</option>
                        <option value="photographie_design_graphique">{t("programme.photographie_design_graphique")}</option>
                        <option value="sciences_nature">{t("programme.sciences_nature")}</option>
                        <option value="sciences_humaines_administration_economie">{t("programme.sciences_humaines_administration_economie")}</option>
                        <option value="sciences_humaines_individu_relations_humaines">{t("programme.sciences_humaines_individu_relations_humaines")}</option>
                        <option value="sciences_humaines_monde_en_action">{t("programme.sciences_humaines_monde_en_action")}</option>
                        <option value="soins_infirmiers">{t("programme.soins_infirmiers")}</option>
                        <option value="soins_infirmiers_auxiliaires">{t("programme.soins_infirmiers_auxiliaires")}</option>
                        <option value="techniques_education_enfance">{t("programme.techniques_education_enfance")}</option>
                        <option value="techniques_bureautique">{t("programme.techniques_bureautique")}</option>
                        <option value="techniques_comptabilite_gestion">{t("programme.techniques_comptabilite_gestion")}</option>
                        <option value="techniques_informatique">{t("programme.techniques_informatique")}</option>
                        <option value="techniques_travail_social">{t("programme.techniques_travail_social")}</option>
                        <option value="technologie_architecture">{t("programme.technologie_architecture")}</option>
                        <option value="technologie_estimation_evaluation_batiment">{t("programme.technologie_estimation_evaluation_batiment")}</option>
                        <option value="technologie_genie_civil">{t("programme.technologie_genie_civil")}</option>
                        <option value="technologie_genie_electrique">{t("programme.technologie_genie_electrique")}</option>
                        <option value="technologie_genie_physique">{t("programme.technologie_genie_physique")}</option>
                        <option value="tremplin_dec">{t("programme.tremplin_dec")}</option>
                    </select>
                    {formSubmitted && <p className={"field-invalid-text"}>{errors.departement}</p>}
                </div>
                <br/>
                <div className="form-dock">
                    <button onClick={() => {switchStep(false)}}><Icon path={mdiChevronLeft} size={1}/></button>
                    <div className={"toolbar-spacer"}></div>
                    <button type="submit" className={"btn-filled"}>{t("information_professeur_page.continue")}<Icon path={mdiChevronRight} size={1}/></button>
                </div>
            </form>
        </div>
    );
}

export default InformationsProfesseur;