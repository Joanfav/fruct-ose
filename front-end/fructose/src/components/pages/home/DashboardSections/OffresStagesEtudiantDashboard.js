import DashboardHome from "../DashboardHome";
import {Link} from "react-router-dom";
import Icon from "@mdi/react";
import {
    mdiArrowRight,
    mdiBriefcaseRemoveOutline,
    mdiChevronRight
} from "@mdi/js";
import React, {useContext, useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {OffreStageContext} from "../../../providers/OffreStageProvider";
import {AuthContext} from "../../../providers/AuthProvider";

const OffresStagesDashboard = () => {

    const {t} = useTranslation()
    const {fetchOffresStage} = useContext(OffreStageContext);
    const [offresStage, setOffresStage] = useState(null);
    const {currentUser} = useContext(AuthContext);

    useEffect(() => {
        if (currentUser) {
            (async function () {
                if (currentUser.role === "ETUDIANT") {
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

    if (offresStage === null) {
        return (
            <section>
                <div className={"toolbar-items"}>
                    <h4 className={"m-0 toolbar-spacer"}>{t("dashboard_home_page.my_offers")}</h4>
                </div>
                <br/>
                <div className="card" style={{height: "420px"}}>
                    <div className="loader-container">
                        <div className="loader"></div>
                    </div>
                </div>
            </section>
        )
    }
    if (offresStage.length !== 0) {
        return (
            <section>
                <div className={"toolbar-items"}>
                    <h4 className={"m-0 toolbar-spacer"}>{t("dashboard_home_page.my_offers")}</h4>
                    <Link to="/dashboard/discover-offers">
                        <button>{t("dashboard_home_page.explore")}
                            <Icon path={mdiChevronRight} size={1}/>
                        </button>
                    </Link>
                </div>
                <br/>
                <div>
                    <div style={{display: "flex", gap: "10px"}}>
                        {
                            (offresStage.length > 0) ?
                                <Link to={`/dashboard/discover-offers?offer=${offresStage[0].id}`}
                                      style={{width: "66%"}}>
                                    <div className="card no-border" style={{height: "300px"}}>
                                        <div className="card-image" style={{backgroundColor: "#21277c"}}>
                                            <div className="card-image-shadowed-text">
                                                <h4>{t("programme." + offresStage[0].departementDTO.nom)}</h4>
                                            </div>
                                            <div className="card-image-information toolbar-items"
                                                 style={{alignItems: "flex-end"}}>
                                                <div className="toolbar-spacer card-image-information-content">
                                                    <h3>{offresStage[0].poste}</h3>
                                                    <h6>{offresStage[0].ownerDTO.companyName}</h6>
                                                </div>
                                                <Icon path={mdiArrowRight} size={1}/>
                                            </div>
                                        </div>
                                    </div>
                                </Link>
                                : null
                        }
                        {
                            (offresStage.length > 1) ?
                                <Link to={`/dashboard/discover-offers?offer=${offresStage[1].id}`}
                                      style={{width: "33%"}}>
                                    <div className="card no-border" style={{height: "300px"}}>
                                        <div className="card-image"
                                             style={{backgroundColor: "#dedede", color: "black"}}>
                                            <div className="card-image-shadowed-text">
                                                <h6>{t("programme." + offresStage[1].departementDTO.nom)}</h6>
                                            </div>
                                            <div className="card-image-information toolbar-items">
                                                <div className="toolbar-spacer card-image-information-content">
                                                    <h4>{offresStage[1].poste}</h4>
                                                    <h6>{offresStage[1].ownerDTO.companyName}</h6>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </Link>
                                : null
                        }
                    </div>
                    <div style={{height: "10px"}}></div>
                    <div style={{display: "flex", gap: "10px"}}>
                        {offresStage.slice(2, 5).map((item, index) => (
                            <Link to={`/dashboard/discover-offers?offer=${item.id}`} key={index} style={{width: "33%"}}>
                                <div className="card no-border" style={{height: "170px"}}>
                                    <div className="card-image" style={{backgroundColor: "#d1d1d1", color: "black"}}>
                                        <div className="card-image-shadowed-text">
                                            <h6>{t("programme." + item.departementDTO.nom)}</h6>
                                        </div>
                                        <div className="card-image-information toolbar-items">
                                            <div className="toolbar-spacer card-image-information-content">
                                                <h5>{item.poste}</h5>
                                                <h6>{item.ownerDTO.companyName}</h6>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </Link>
                        ))}
                    </div>
                </div>
            </section>
        );
    } else {
        return (
            <section>
                <div className={"toolbar-items"}>
                    <h4 className={"m-0 toolbar-spacer"}>{t("dashboard_home_page.my_offers")}</h4>
                </div>
                <br/>
                <div className="card">
                    <div style={{
                        "height": "420px",
                        "display": "flex",
                        "alignItems": "center",
                        "justifyContent": "center",
                        "backgroundColor": "#eee",
                        "borderRadius": "5px"
                    }}>
                        <div className="no-items-display">
                            <Icon path={mdiBriefcaseRemoveOutline} size={1.5}/>
                            <h6>{t("dashboard_home_page.no_offers")}</h6>
                        </div>
                    </div>
                </div>
            </section>
        );
    }

}

export default OffresStagesDashboard;