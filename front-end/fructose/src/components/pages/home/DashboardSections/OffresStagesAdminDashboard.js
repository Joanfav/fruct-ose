import {Link} from "react-router-dom";
import Icon from "@mdi/react";
import {
    mdiBriefcaseOutline, mdiBriefcasePlusOutline,
    mdiBriefcaseRemoveOutline, mdiChevronLeft,
    mdiChevronRight, mdiClose
} from "@mdi/js";
import React, {useContext, useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {OffreStageContext} from "../../../providers/OffreStageProvider";
import {AuthContext} from "../../../providers/AuthProvider";
import OfferPreview from "../../offre-stage/OfferPreview";
import Swal from "sweetalert2";

const OffresStagesAdminDashboard = () => {

    const {t} = useTranslation()
    const {fetchOffresStage} = useContext(OffreStageContext);
    const [offresStage, setOffresStage] = useState(null);
    const {currentUser, currentToken} = useContext(AuthContext);

    const [currentOffer, setCurrentOffer] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 6;

    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;

    const offresStageInPage = (offresStage)?offresStage.slice(startIndex, endIndex):null;
    const maxPages = (offresStage)?Math.ceil(offresStage.filter(offre => !offre.isApproved && !offre.isRefused).length / itemsPerPage):1;


    useEffect(() => {
        if (currentUser) {
            (async function () {
                try {
                    const response = await fetchOffresStage();
                    setOffresStage(response.filter(offre => !offre.isApproved && !offre.isRefused));
                } catch (error) {
                    console.log("error" + error);
                }
            })();
        }
    }, [currentUser, fetchOffresStage]);

    const handleValidateOffer = (id) => {
        fetch(`/accepter-offre-stage/` + id, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": currentToken
            }
        })
            .then(response => {
                if (response.ok) {
                    setOffresStage((prevOffreStages) => {
                        const updatedOffres = prevOffreStages.filter((offreStage) => offreStage.id !== id);
                        if (updatedOffres.length === 0) {
                            setCurrentOffer(null);
                        }
                        return updatedOffres;
                    });
                    setCurrentOffer(null);
                } else {
                    throw new Error("Erreur lors de l'acceptation de l'offre");
                }
            })
            .catch(error => {
                console.error("Erreur lors de l'acceptation de l'offre:", error);
            });
    }

    const handleRejectOffer = (id, string) => {
        fetch(`/refuser-offre-stage/` + id, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": currentToken
            },
            body: string,
        })
            .then(response => {
                if (response.ok) {
                    setOffresStage((prevOffreStages) => {
                        const updatedOffres = prevOffreStages.filter((offreStage) => offreStage.id !== id);
                        if (updatedOffres.length === 0) {
                            setCurrentOffer(null);
                        }
                        return updatedOffres;
                    });
                    setCurrentOffer(null);
                    Swal.fire({
                        title: "Success",
                        text: t("dashboard_home_page.offer_rejected_success"),
                        icon: "success"
                    });
                } else {
                    throw new Error("Erreur lors du refus de l'offre");
                }
            })
            .catch(error => {
                console.error("Erreur lors du refus de l'offre:", error);
                Swal.fire({
                    title: "Oops...",
                    text: t("dashboard_home_page.no_comment_reject_offer_text"),
                    icon: "error"
                });
            });
    };

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
                                <OfferPreview currentOffer={currentOffer} handleValidate={handleValidateOffer}
                                              handlerefused={handleRejectOffer} style={{
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

    return(
        <>
            {getOfferWindow()}
            <section>
                <div className="toolbar-items">
                    <h4 className="m-0 toolbar-spacer">{t("dashboard_home_page.manage_offers")}</h4>
                    <Link to="/dashboard/creer-offre-stage">
                        <button className={"btn-filled"}>
                            <Icon path={mdiBriefcasePlusOutline} size={1}/> {t("dashboard_home_page.add_offer")}
                        </button>
                    </Link>
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
                                    <div className="menu-list-item" key={index} onClick={() => setCurrentOffer(offre)}>
                                        <Icon path={mdiBriefcaseOutline} size={1}/>
                                        <div>
                                            <h6 className="m-0">{offre.nom}</h6>
                                            <p className="m-0 text-dark">{offre.ownerDTO.companyName}</p>
                                        </div>
                                        <div className="toolbar-spacer"></div>
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

export default OffresStagesAdminDashboard;