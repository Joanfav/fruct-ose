import React from "react";
import {mdiChevronRight, mdiDomain, mdiHumanMaleBoard, mdiSchool} from "@mdi/js";
import Icon from "@mdi/react";
import {useTranslation} from "react-i18next";

const SelectionRole = ({utilisateur, handleChange, switchStep}) => {

    const {t} = useTranslation();

    const handleSubmit = (event) => {
        // SI TU TOUCHES UPPERCASE LE WEBSITE VA EXPLOSER!
        utilisateur.role = utilisateur.role.toUpperCase();
        event.preventDefault();
        const errorMessage = validateFields();
        if (errorMessage) {
            console.log(errorMessage)
        } else {
            switchStep(true)
        }
    };

    const validateFields = () => {
        if (!/^[A-Za-z\s]+$/.test(utilisateur.role)) {
            return "Le rôle doit contenir uniquement des lettres et des espaces";
        }
    }

    return (
        <div>
            <h4>{t("selection_role_page.who_are_you")}</h4>
            <p>{t("selection_role_page.description")}</p>
            <br/>
            <form onSubmit={handleSubmit}>
                <div style={{"margin": "auto"}}>
                    <div className={"toolbar-items"} style={{"gap": "12px"}}>
                        <div style={{
                            "width": "30%",
                            "border": "1px solid grey",
                            "borderRadius": "5px",
                            "textAlign": "center",
                            "padding": "10px"
                        }}>
                            <Icon path={mdiSchool} size={2}/>
                            <br/>
                            <h6>
                                <input id="etudiant" type="radio" name="role" value="Etudiant" onChange={handleChange} checked={utilisateur.role === "Etudiant"}/>{t("selection_role_page.student")}
                            </h6>
                        </div>
                        <div style={{
                            "width": "30%",
                            "border": "1px solid grey",
                            "borderRadius": "5px",
                            "textAlign": "center",
                            "padding": "10px"
                        }}>
                            <Icon path={mdiDomain} size={2}/>
                            <br/>
                            <h6>
                                <input id="employeur" type="radio" name="role" value="Employeur" onChange={handleChange} checked={utilisateur.role === "Employeur"}/>{t("selection_role_page.employer")}
                            </h6>
                        </div>
                        <div style={{
                            "width": "30%",
                            "border": "1px solid grey",
                            "borderRadius": "5px",
                            "textAlign": "center",
                            "padding": "10px"
                        }}>
                            <Icon path={mdiHumanMaleBoard} size={2}/>
                            <br/>
                            <h6>
                                <input id="professeur" type="radio" name="role" value="Professeur" onChange={handleChange} checked={utilisateur.role === "Professeur"}/>{t("selection_role_page.teacher")}
                            </h6>
                        </div>

                    </div>
                </div>
                <div className="form-dock">
                    <div className={"toolbar-spacer"}></div>
                    <button type="submit" className={"btn-filled"}>{t("selection_role_page.continue")}<Icon path={mdiChevronRight} size={1}/></button>
                </div>
            </form>
        </div>
    );
}

export default SelectionRole;