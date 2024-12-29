import DashboardHome from "../DashboardHome";
import {Link} from "react-router-dom";
import Icon from "@mdi/react";
import {
    mdiBriefcaseOutline,
    mdiBriefcasePlusOutline,
    mdiBriefcaseRemoveOutline,
    mdiCheck,
    mdiCheckCircleOutline,
    mdiChevronLeft,
    mdiChevronRight,
    mdiClockOutline,
    mdiClose
} from "@mdi/js";
import React, {useContext, useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import OfferPreview from "../../offre-stage/OfferPreview";
import {OffreStageContext} from "../../../providers/OffreStageProvider";
import {AuthContext} from "../../../providers/AuthProvider";

const OffresStagesEmpProfDashboard = () => {

    const {currentUser} = useContext(AuthContext);
    const {fetchOffresStage, deleteOffreStage} = useContext(OffreStageContext);
    const [offresStage, setOffresStage] = useState([]);
    const {t} = useTranslation();
    const [currentOffer, setCurrentOffer] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 6;

    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;

    const offresStageInPage = (offresStage)?offresStage.slice(startIndex, endIndex):null;
    const maxPages = (offresStage)?Math.ceil(offresStage.length / itemsPerPage):1;
    const selectedOffresStage = offresStageInPage

    useEffect(() => {
        if (currentUser) {
            (async function () {
                if (currentUser.role === "EMPLOYEUR"|| currentUser.role === "PROFESSEUR") {
                    try {
                        const response = await fetchOffresStage();
                        setOffresStage(response);
                    } catch (error) {
                        console.log("error" + error);
                    }
                }
            })();
        }
    }, [currentUser, fetchOffresStage]);

    const handlePageChange = (isNext) => {
        if (isNext) {
            if (currentPage + 1 <= maxPages) {
                setCurrentPage(currentPage + 1)
            }
        } else {
            if (currentPage - 1 >= 1) {
                setCurrentPage(currentPage - 1)
            }
        }
    };

    const handleDeleteOffreStage = async (offreStageId) => {
        try {
            const response = await deleteOffreStage(offreStageId);
            if (response.ok) {
                setOffresStage((prevOffreStages) => prevOffreStages.filter((offreStage) => offreStage.id !== offreStageId));
                setCurrentOffer(null);
            } else {
                console.error("Error deleting offre stage:", response.statusText);
            }
        } catch (error) {
            console.error("Error deleting offre stage:", error);
        }
    };

    const getOfferWindow = () => {
        if(currentOffer){
            return(
                <>
                    <div className="window-frame">
                        <div className="window">
                            <div className="window-titlebar">
                                <h5>{t("dashboard_home_page.offer")}</h5>
                                <span className="toolbar-spacer"></span>
                                <button className="btn-icon" onClick={() => setCurrentOffer(null)}>
                                    <Icon path={mdiClose} size={1}/>
                                </button>
                            </div>
                            <div className="window-content">
                                <OfferPreview
                                    currentOffer={currentOffer}
                                    handleDeleteOffreStage={handleDeleteOffreStage}
                                    style={{
                                        flex: 2,
                                        padding: "10px",
                                        backgroundColor: "#fff",
                                        borderRadius: "5px",
                                        boxShadow: "0 0 10px rgba(0,0,0,0.1)"
                                    }}/>
                            </div>
                        </div>
                    </div>
                </>
            )
        }
    }

    return (
        <>
            {getOfferWindow()}
            <section>
                <div className="toolbar-items">
                    <h4 className={"m-0 toolbar-spacer"}>{t("dashboard_home_page.offers")}</h4>
                    {currentUser.role === "EMPLOYEUR" && (
                        <Link to="/dashboard/creer-offre-stage">
                            <button className={"btn-filled"}>
                                <Icon path={mdiBriefcasePlusOutline} size={1}/>
                                {t("dashboard_home_page.add_offer")}
                            </button>
                        </Link>
                    )}
                </div>
                <br/>
                <div className="menu-list">
                    {
                        (!offresStage || offresStage.length === 0)?
                            <div className="menu-list-item menu-list-empty-list-placeholder">
                                {(!offresStage)?
                                    <div className="loader"></div>
                                    :
                                    <div className="no-items-display">
                                        <Icon path={mdiBriefcaseRemoveOutline} size={1.5} />
                                        <h6>{t("discover_offers_page.no_offers")}</h6>
                                        <p className="text-dark text-mini">{t("discover_offers_page.future_offers")}</p>
                                    </div>}
                            </div>
                            :
                            <>
                                {offresStageInPage.map((offre, index) => (
                                    <div className={"menu-list-item " + ((!offre.isApproved && !offre.isRefused)?"menu-list-item-pending":((offre.isRefused)?"menu-list-item-striked":""))} key={index} onClick={() => setCurrentOffer(offre)}>
                                        <Icon path={mdiBriefcaseOutline} size={1}/>
                                        <div>
                                            <div className="toolbar-items">
                                                <h6 className="m-0">{offre.nom }</h6>
                                                {currentUser.role === "EMPLOYEUR" && offre && (
                                                    <>
                                                        {offre.isApproved ? (
                                                            <Icon path={mdiCheckCircleOutline} size={0.7} />
                                                        ) : offre.isRefused ? (
                                                            <Icon path={mdiClose} size={0.7} className="text-red" />
                                                        ) : (
                                                            <Icon path={mdiClockOutline} size={0.7} />
                                                        )}
                                                    </>
                                                )}
                                            </div>
                                            <p className="m-0 text-dark">{offre.ownerDTO.companyName}</p>
                                        </div>
                                    </div>
                                ))}

                                {
                                    (offresStageInPage.length < itemsPerPage)
                                        ?
                                        Array.from({length: itemsPerPage - offresStageInPage.length}, (_, i) => (
                                            <div key={i} className="menu-list-item menu-list-item-placeholder">
                                            </div>
                                        ))
                                        :
                                        null
                                }
                            </>
                    }

                    <div className="menu-list-item menu-list-footer">
                        <p className="m-0">{(offresStage)?offresStage.length:1} {t("discover_offers_page.results")}</p>
                        <div className="toolbar-spacer"></div>
                        <p className="m-0">{currentPage} {t("dashboard_home_page.of")} {maxPages}</p>
                        <button className="btn-icon" disabled={currentPage === 1} onClick={() => {
                            handlePageChange(false)
                        }}><Icon path={mdiChevronLeft} size={1}/></button>
                        <button className="btn-icon" disabled={!(currentPage < maxPages)} onClick={() => {
                            handlePageChange(true)
                        }}><Icon path={mdiChevronRight} size={1}/></button>
                    </div>
                </div>
            </section>
        </>
    )
}

export default OffresStagesEmpProfDashboard;