import {Link, useNavigate} from "react-router-dom";
import Icon from "@mdi/react";
import {
    mdiAccountSchoolOutline,
    mdiBriefcaseCheckOutline, mdiBriefcaseOutline, mdiBriefcaseRemoveOutline,
    mdiChevronRight, mdiClose,
    mdiFileSign,
    mdiPresentation,
    mdiTooltipPlusOutline
} from "@mdi/js";
import React, {useContext, useEffect, useState} from "react";
import {AuthContext} from "../../../providers/AuthProvider";
import {useTranslation} from "react-i18next";

const UserManagementDashboard = () => {
    const {t} = useTranslation();
    const {currentUser, isUserInit} = useContext(AuthContext);
    const {currentToken} = useContext(AuthContext);
    const [candidatures, setCandidatures] = useState(null);
    const itemsPerPage = 6;
    const navigate = useNavigate()


    useEffect(() => {
        if(currentUser){
            fetch("/candidatures/candidaturesEmployeur", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": currentToken
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    setCandidatures(data.slice(0,itemsPerPage));
                })
                .catch(error => console.error("Error fetching candidatures", error));
        }
    }, [currentUser]);

    const goToCandidaturesPage = (section) => {
        navigate("/dashboard/view-candidatures?section=" + section)
    }

    if (currentUser != null) {
        if (currentUser.role === "EMPLOYEUR") {
            return (
                <section>
                    <h4 className={"m-0 toolbar-spacer"}>Candidatures</h4>
                    <br/>
                    <div className="toolbar-items" style={{gap: "10px", alignItems: "flex-start"}}>
                        <div style={{width: "45%"}}>
                            <div className="card card-clickable" style={{padding: "0 10px"}} onClick={()=>{goToCandidaturesPage("nouvelles_candidatures")}}>
                               <div className="list-bullet">
                                   <Icon path={mdiTooltipPlusOutline} size={1} />
                                   <div>
                                       <h5 >{t("view_candidatures_page.new_applications")}</h5>
                                       <p className="text-dark">{t("view_candidatures_page.categorie_selection")}</p>
                                   </div>
                               </div>
                            </div>
                            <br/>
                            <div className="card card-clickable" style={{padding: "0 10px"}} onClick={()=>{goToCandidaturesPage("en_entrevue")}}>
                                <div className="list-bullet">
                                    <Icon path={mdiPresentation} size={1} />
                                    <div>
                                        <h5 className="m-0">{t("view_candidatures_page.in_interview")}</h5>
                                    </div>
                                </div>
                            </div>
                            <br/>
                            <div className="card card-clickable" style={{padding: "0 10px"}}  onClick={()=>{goToCandidaturesPage("en_signature")}}>
                                <div className="list-bullet">
                                    <Icon path={mdiFileSign} size={1} />
                                    <div>
                                        <h5 className="m-0">{t("view_candidatures_page.in_contract_signature")}</h5>
                                    </div>
                                </div>
                            </div>
                            <br/>
                            <div className="card card-clickable" style={{padding: "0 10px"}}  onClick={()=>{goToCandidaturesPage("accepte_total")}}>
                                <div className="list-bullet">
                                    <Icon path={mdiBriefcaseCheckOutline} size={1} />
                                    <div>
                                        <h5 className="m-0">{t("view_candidatures_page.accepted_position")}</h5>
                                    </div>
                                </div>
                            </div>
                            <br/>
                            <div className="card card-clickable" style={{padding: "0 10px"}}  onClick={()=>{goToCandidaturesPage("rejete_total")}}>
                                <div className="list-bullet">
                                    <Icon path={mdiBriefcaseRemoveOutline} size={1} />
                                    <div>
                                        <h5 className="m-0">{t("view_candidatures_page.rejected_applications")}</h5>
                                    </div>
                                </div>
                            </div>

                        </div>
                        <div style={{width: "65%"}}>
                            <div className="menu-list">
                                {
                                    (!candidatures || candidatures.length === 0)?
                                        <div className="menu-list-item menu-list-empty-list-placeholder">
                                            {(!candidatures)?
                                                <div className="loader"></div>
                                                :
                                                <div className="no-items-display">
                                                    <Icon path={mdiBriefcaseRemoveOutline} size={1.5} />
                                                    <h6>{t("view_candidatures_page.no_results")}</h6>
                                                    <p className="text-dark text-mini">{t("view_candidatures_page.no_results_description")}</p>
                                                </div>}
                                        </div>
                                        :
                                        <>
                                            {candidatures.map((candidature, index) => (
                                                <div className={"menu-list-item"} key={index} onClick={() => goToCandidaturesPage("all")}>
                                                    <Icon path={mdiAccountSchoolOutline} size={1} />
                                                    <div>
                                                        <div className="toolbar-items">
                                                            <h6 className="m-0">{candidature.candidature.etudiantDTO.fullName }</h6>
                                                        </div>
                                                        <p className="m-0 text-dark">{candidature.candidature.offreStageDTO.nom}</p>
                                                    </div>
                                                    <div className="toolbar-spacer"></div>
                                                    {candidatures[index].candidature.etat === "ENTREVUE_ACCEPTE_ETUDIANT" && (
                                                        <span className="badge bg-orange" style={{marginLeft: "10px"}}>{t("view_candidatures_page.action_needed")}</span>
                                                    )}
                                                    {
                                                        (candidature.candidature.etat === "REFUSEE" || candidature.candidature.etat === "REFUSEE_APRES_ENTREVUE")?
                                                            <Icon path={mdiBriefcaseRemoveOutline} size={1} className="text-dark" />
                                                            :
                                                            (candidature.candidature.etat === "POSTE_OBTENU")?
                                                                <Icon path={mdiBriefcaseCheckOutline} size={1} className="text-dark" />
                                                                :
                                                                (candidatures[index].candidature.etat === "EN_ATTENTE")?
                                                                    <Icon path={mdiTooltipPlusOutline} size={1} className="text-dark" />
                                                                    :null
                                                    }
                                                </div>
                                            ))}

                                            {
                                                (candidatures.length < itemsPerPage)
                                                    ?
                                                    Array.from({length: itemsPerPage - candidatures.length}, (_, i) => (
                                                        <div key={i} className="menu-list-item menu-list-item-placeholder">
                                                        </div>
                                                    ))
                                                    :
                                                    null
                                            }
                                        </>
                                }

                                <div className="menu-list-item menu-list-view-more " onClick={()=>{goToCandidaturesPage("all")}}>
                                    <div className="toolbar-spacer"></div>
                                    <p className="m-0">{t("dashboard_home_page.see_all")}</p>
                                    <Icon path={mdiChevronRight} size={1}/>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>
            );
        }
    }
}

export default UserManagementDashboard