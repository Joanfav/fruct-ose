import {Link, useNavigate} from "react-router-dom";
import Icon from "@mdi/react";
import {
    mdiCheck, mdiCheckCircleOutline,
    mdiChevronRight,
    mdiClockOutline,
    mdiClose, mdiCloseCircleOutline,
    mdiFileDocumentOutline, mdiFileQuestionOutline,
    mdiPlus
} from "@mdi/js";
import React, {useContext, useEffect, useState} from "react";
import {CvContext} from "../../../providers/CvProvider";
import {useTranslation} from "react-i18next";
import {AuthContext} from "../../../providers/AuthProvider";


const CVEtudiantDashboard = () => {
    const {GetCvs} = useContext(CvContext);
    const [cvs, setCvs] = useState(null);
    const {t} = useTranslation();
    const {currentUser} = useContext(AuthContext);
    const navigate = useNavigate();

    const handleCvClick = (cv) => {
        navigate(`/dashboard/manage-cvs?cv=${cv.id}`);
    };

    const goToCvPage = () => {
        navigate("/dashboard/manage-cvs");
    };

    useEffect(() => {
        if (currentUser) {
            (async function () {
                if (currentUser.role === "ETUDIANT") {
                    try {
                        const response = await GetCvs();
                        const data = await response.json();
                        setCvs(data.reverse());
                    } catch (error) {
                        console.log("Erreur lors de la récupération des CVs : " + error);
                    }
                }
            })();
        }
    }, [currentUser, GetCvs]);

    const itemsPerPage = 4;
    const cvsInPage = (cvs)?cvs.slice(0, itemsPerPage):null;

    if (currentUser) {
        if (currentUser.role === "ETUDIANT") {
            return (
                <div>
                    <section>
                        <div className={"toolbar-items"}>
                            <h4 className={"m-0 toolbar-spacer"}>{t("dashboard_home_page.portfolio")}</h4>
                            <Link to="/dashboard/upload-cv">
                                <button>{t("dashboard_home_page.add_cv")}
                                    <Icon path={mdiPlus} size={1}/>
                                </button>
                            </Link>
                        </div>
                        <br/>
                        {
                            (cvs && cvs.length > 0) ?
                                <>
                                    <div className="menu-list">
                                            <div
                                                onClick={() => handleCvClick(cvsInPage[0])}
                                                className={"menu-list-item p-0 " + ((!cvsInPage[0].isApproved && !cvsInPage[0].isRefused)?"menu-list-item-pending":((cvsInPage[0].isRefused)?"menu-list-item-striked":""))}
                                                style={{height: "132px",}}>
                                                <div className="menu-list-item-image">
                                                    <img src="/assets/dashboard/preview.png" alt=""
                                                         style={{height: "100%"}}/>
                                                </div>
                                                <div style={{paddingLeft: "14px"}}>
                                                    <h5 className="m-0">{cvsInPage[0].filename} </h5>
                                                </div>
                                                {!cvsInPage[0].isApproved && !cvsInPage[0].isRefused &&
                                                    <Icon path={mdiClockOutline} size={0.9} />}
                                                {cvsInPage[0].isApproved &&
                                                    <Icon path={mdiCheckCircleOutline} size={0.9} />}
                                                {cvsInPage[0].isRefused &&
                                                    <Icon path={mdiCloseCircleOutline} size={0.9} />}
                                            </div>
                                    </div>
                                    <br/>
                                </>
                                : null
                        }

                        <div className="menu-list">
                            {
                                (!cvs || cvs.length === 0) ?
                                    <div className="menu-list-item menu-list-empty-list-placeholder">
                                        {(!cvs)?
                                            <div className="loader"></div>
                                        :
                                        <div className="no-items-display">
                                            <Icon path={mdiFileQuestionOutline} size={1.4}/>
                                            <h6>{t("manage_cv.messages.no_cvs")}</h6>
                                            <p className="text-dark text-mini">Ajoutez votre CV pour postuler</p>
                                        </div>}
                                    </div>
                                    :
                                    <>
                                        {cvsInPage.slice(1).map((cv, index) => (
                                            <div className={"menu-list-item " + ((!cv.isApproved && !cv.isRefused)?"menu-list-item-pending":((cv.isRefused)?"menu-list-item-striked":""))} key={index}
                                                 onClick={() => handleCvClick(cv)}>
                                                <Icon path={mdiFileDocumentOutline} size={1}/>
                                                <h6 className="m-0">{cv.filename} </h6>
                                                {!cv.isApproved && !cv.isRefused &&
                                                    <Icon path={mdiClockOutline} size={0.7} />}
                                                {cv.isApproved &&
                                                    <Icon path={mdiCheckCircleOutline} size={0.7} />}
                                                {cv.isRefused &&
                                                    <Icon path={mdiClose} size={0.7} className="text-red" />}
                                            </div>
                                        ))}

                                        {
                                            (cvsInPage.length < itemsPerPage)
                                                ?
                                                Array.from({length: itemsPerPage - cvsInPage.length}, (_, i) => (
                                                    <div key={i}
                                                         className="menu-list-item menu-list-item-placeholder">
                                                    </div>
                                                ))
                                                :
                                                null
                                        }
                                    </>
                            }

                            <div className="menu-list-item menu-list-view-more " onClick={()=>{goToCvPage()}}>
                                <div className="toolbar-spacer"></div>
                                <p className="m-0">{t("dashboard_home_page.see_all")}</p>
                                <Icon path={mdiChevronRight} size={1}/>
                            </div>
                        </div>
                    </section>
                </div>
            )
        }
    }
}
export default CVEtudiantDashboard;