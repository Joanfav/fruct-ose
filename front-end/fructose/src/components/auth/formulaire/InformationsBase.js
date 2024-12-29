import React, {useEffect, useState} from "react";
import {mdiChevronLeft, mdiChevronRight} from "@mdi/js";
import Icon from "@mdi/react";
import {useTranslation} from "react-i18next";
import {isEmailTaken} from "../../../utilities/api/apiService";
import Swal from "sweetalert2";

const InformationsBase = ({utilisateur, handleChange, switchStep}) => {

    const {t} = useTranslation();
    const [errors, setErrors] = useState({});
    const [formSubmitted, setFormSubmitted] = useState(false);

    const handleSubmit = async (event) => {
        event.preventDefault();
        setFormSubmitted(true);
        const errorMessage = validateFields();
        const emailTaken = await isEmailTaken(utilisateur.email);
        if (emailTaken) {
            errorMessage.email = t("information_base_page.error.email_taken");
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: t("information_base_page.error.email_taken"),
                showConfirmButton: true
            });
        }
        if (Object.keys(errorMessage).length > 0) {
            setErrors(errorMessage);
        } else {
            switchStep(true);
        }
    };

    const handleInputChange = (event) => {
        const {name} = event.target;
        handleChange(event);
        setErrors((prevErrors) => ({...prevErrors, [name]: ""}));
    };

    const validateFields = () => {
        let errors = {};

        if (utilisateur.firstName.length < 2) {
            errors.firstName = t("information_base_page.error.first_name_short");
        } else if (!/^[A-Za-z\s]+$/.test(utilisateur.firstName)) {
            errors.firstName = t("information_base_page.error.first_name_letters_only");
        }

        if (utilisateur.lastName.length < 2) {
            errors.lastName = t("information_base_page.error.last_name_short");
        } else if (!/^[A-Za-z\s]+$/.test(utilisateur.lastName)) {
            errors.lastName = t("information_base_page.error.last_name_letters_only");
        }

        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(utilisateur.email)) {
            errors.email = t("information_base_page.error.email");
        }

        return errors;
    };

    useEffect(() => {
        if (formSubmitted) {
            setErrors((prevErrors) => {
                const updatedErrors = {...prevErrors};

                if (utilisateur.firstName.length < 2) {
                    updatedErrors.firstName = t("information_base_page.error.first_name_short");
                } else if (!/^[A-Za-z\s]+$/.test(utilisateur.firstName)) {
                    updatedErrors.firstName = t("information_base_page.error.first_name_letters_only");
                } else {
                    delete updatedErrors.firstName;
                }

                if (utilisateur.lastName.length < 2) {
                    updatedErrors.lastName = t("information_base_page.error.last_name_short");
                } else if (!/^[A-Za-z\s]+$/.test(utilisateur.lastName)) {
                    updatedErrors.lastName = t("information_base_page.error.last_name_letters_only");
                } else {
                    delete updatedErrors.lastName;
                }

                if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(utilisateur.email)) {
                    updatedErrors.email = t("information_base_page.error.email");
                } else if (prevErrors.email === t("information_base_page.error.email_taken")) {
                    updatedErrors.email = t("information_base_page.error.email_taken");
                } else {
                    delete updatedErrors.email;
                }

                return updatedErrors;
            });
        }
    }, [utilisateur.firstName, utilisateur.lastName, utilisateur.email, t, formSubmitted]);

    return (
        <div className={"form-signup-condensed"}>
            <h4>{t("information_base_page.information")}</h4>
            <p>{t("information_base_page.description")}</p>
            <br/>
            <form onSubmit={handleSubmit}>
                <div className={"input-container"}>
                    <p>{t("information_base_page.first_name")}:</p>
                    <input className={`${errors.firstName ? "field-invalid" : ""}`} type="text" name="firstName" required value={utilisateur.firstName} onChange={handleInputChange}/>
                    {formSubmitted && <p className={"field-invalid-text"}>{errors.firstName}</p>}
                </div>
                <div className={"input-container"}>
                    <p>{t("information_base_page.last_name")}:</p>
                    <input className={`${errors.lastName ? "field-invalid" : ""}`} type="text" name="lastName" required value={utilisateur.lastName} onChange={handleInputChange}/>
                    {formSubmitted && <p className={"field-invalid-text"}>{errors.lastName}</p>}
                </div>
                <div className={"input-container"}>
                    <p>{t("information_base_page.email")}:</p>
                    <input className={`${errors.email ? "field-invalid" : ""}`} type="text" name="email" required value={utilisateur.email} onChange={handleInputChange}/>
                    {formSubmitted && <p className={"field-invalid-text"}>{errors.email}</p>}
                </div>
                <br/>

                <div className="form-dock">
                    <button type="button" onClick={() => {switchStep(false)}}><Icon path={mdiChevronLeft} size={1}/></button>
                    <div className={"toolbar-spacer"}></div>
                    <button type="submit" className={"btn-filled"}>{t("information_base_page.continue")}<Icon path={mdiChevronRight} size={1}/></button>
                </div>
            </form>
        </div>
    );
}

export default InformationsBase;