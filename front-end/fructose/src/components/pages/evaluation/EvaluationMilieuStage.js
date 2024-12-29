import {AuthContext} from "../../providers/AuthProvider";
import React, {useContext, useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {CandidatureContext} from "../../providers/CandidatureProvider";
import Icon from "@mdi/react";
import {
    mdiBriefcasePlusOutline, mdiChevronLeft,
    mdiChevronRight,
    mdiClose,
    mdiFileOutline,
    mdiFileQuestionOutline,
    mdiFileSign
} from "@mdi/js";
import {Link} from "react-router-dom";
import PdfPreview from "../../../utilities/pdf/PdfPreview";

const EvaluationMilieuStage = () => {
    const {currentUser, currentToken} = useContext(AuthContext);
    const {t} = useTranslation();
    const {fetchCandidatureByEtatContratSigneTous} = useContext(CandidatureContext);

    const [candidatures, setCandidatures] = useState([]);
    const [evaluation, setEvaluation] = useState([]);
    const [currentEvaluation, setCurrentEvaluation] = useState(null);
    const [evaluationPdfUrl, setEvaluationPdfUrl] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 6;
    const maxPages = Math.max(1, Math.ceil(candidatures.length / itemsPerPage));

    useEffect(() => {
        if (currentUser && currentUser.role === "ADMIN") {
            fetchCandidatureByEtatContratSigneTous()
                .then((data) => setCandidatures(data || []))
                .catch((error) => {
                    console.error("Erreur lors de la récupération des données :", error);
                    setCandidatures([]);
                });
            fetchAllEvaluations()
                .then((data) => setEvaluation(data || []))
                .catch((error) => {
                    console.error("Erreur lors de la récupération des données :", error);
                    setEvaluation([]);
                });
        }
    }, [currentUser]);

    const fetchAllEvaluations = async () => {
        try {
            const response = await fetch(`/evaluations/milieu-stage/all-evaluation`, {
                method: "GET", headers: {
                    "Content-Type": "application/json", Authorization: currentToken,
                },
            });
            if (!response.ok) {
                throw new Error("Erreur lors de la récupération de l'évaluation.");
            }
            const data = await response.json();
            return data;
        } catch (error) {
            console.error("Erreur : ", error);
            throw error;
        }
    };

    const fetchEvaluationByCandidatureId = async (evaluation) => {
        try {
            const response = await fetch(`/evaluations/milieu-stage/${evaluation.id}`, {
                method: "GET", headers: {
                    "Content-Type": "application/json", "Authorization": currentToken,
                },
            });
            if (!response.ok) {
                throw new Error("Erreur lors de la récupération de l'évaluation.");
            }
            return window.URL.createObjectURL(await response.blob());
        } catch (error) {
            console.error("Erreur : ", error);
            throw error;
        }
    };

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    const handleDeselect = () => {
        setCurrentEvaluation(null)
        setEvaluationPdfUrl(null)
    }

    if (currentUser?.role === "ADMIN") {
        const startIndex = (currentPage - 1) * itemsPerPage;
        const selectedCandidatures = candidatures.slice(startIndex, startIndex + itemsPerPage);
        const selectedEvaluations = evaluation.slice(startIndex, startIndex + itemsPerPage);

        return (<section>
            <div className="toolbar-items">
                <h4 className="m-0 toolbar-spacer">{t("evaluation_milieu_stage.milieu_stage_a_evaluer")}</h4>
            </div>
            <div style={{padding: "10px 0"}}>
                {candidatures.length === 0 ? (<div
                    style={{
                        alignItems: "center",
                        width: "400px",
                        display: "flex",
                        backgroundColor: "#eee",
                        borderRadius: "5px",
                        gap: "5px",
                        padding: "10px"
                    }}
                >
                    <Icon path={mdiBriefcasePlusOutline} size={1}/>
                    <p className="m-0">{t("evaluation_milieu_stage.no_milieu_stage_a_evaluer")}</p>
                </div>) : (<div
                    className="menu-list"
                >
                    {selectedCandidatures.map((candidature, index) => (<div
                        key={index}
                        className="menu-list-item"
                    >
                        <p style={{margin: 0, flex: 1, textAlign: "left"}}>
                            {candidature.etudiant.fullName}
                        </p>

                        <Link
                            to="/dashboard/evaluation-milieu-stage-step/"
                            state={{candidature}}
                        >
                            <>
                                <button
                                    className="btn-filled"
                                >
                                    {t("evaluation_milieu_stage.evaluer")}
                                    <Icon path={mdiChevronRight} size={1}/>
                                </button>
                            </>
                        </Link>
                    </div>))}
                </div>)}
            </div>
            <br/>
            <div className="toolbar-items">
                <h4>{t("evaluation_milieu_stage.evaluation_milieu_stage")}</h4>
            </div>
            <div className="menu-list">
                {!evaluation && evaluation.length === 0 ? (
                    <div className="menu-list-item menu-list-empty-list-placeholder">
                    {(!evaluation) ?
                        <div className="loader"></div>
                        :
                        <div className="no-items-display">
                            <Icon path={mdiFileQuestionOutline} size={1.4}/>
                            <h6>{t("dashboard_home_page.no_evaluation")}</h6>
                        </div>}
                    </div>
                ) : (<>

                            {selectedEvaluations.map((evalItem) => (<div
                                key={evalItem.id}
                                className="menu-list-item"
                                onClick={() => {
                                    setCurrentEvaluation(evalItem);
                                    fetchEvaluationByCandidatureId(evalItem).then(setEvaluationPdfUrl);
                                }}
                            >
                                <Icon path={mdiFileSign} size={1} />
                                <div>
                                    <h6 className="m-0">{evalItem.candidatureDTO.etudiantDTO.fullName} - {evalItem.candidatureDTO.offreStageDTO.nom}</h6>
                                    <p className="m-0">{evalItem.candidatureDTO.offreStageDTO.compagnie}</p>
                                </div>
                            </div>))}
                    {
                        (selectedEvaluations.length < itemsPerPage)
                            ?
                            Array.from({length: itemsPerPage - selectedEvaluations.length}, (_, i) => (
                                <div key={i}
                                     className="menu-list-item menu-list-item-placeholder">
                                </div>
                            ))
                            :
                            null
                    }
                </>)}
                {currentEvaluation && (
                    <div className="window-frame">
                        <div className="window">
                            <div className="window-titlebar">
                                <h5>{t("dashboard_home_page.manage_evaluations")}</h5>
                                <span className="toolbar-spacer"></span>
                                <button className="btn-icon" onClick={() => handleDeselect()}>
                                    <Icon path={mdiClose} size={1}/>
                                </button>
                            </div>
                            <div className="window-content">
                                <section className="nospace">
                                    <div>
                                        {(evaluationPdfUrl)?<PdfPreview file={evaluationPdfUrl}/>:null}
                                    </div>
                                    <br/>
                                </section>
                            </div>
                        </div>
                    </div>)}
                <div className="menu-list-item menu-list-footer">
                    <p className="m-0">{(evaluation)?evaluation.length:1} {t("discover_offers_page.results")}</p>
                    <div className="toolbar-spacer"></div>
                    <p className="m-0">{currentPage} {t("dashboard_home_page.of")} {1}</p>
                    <button className="btn-icon" disabled={currentPage === maxPages} onClick={() => {
                        handlePageChange(false)
                    }}><Icon path={mdiChevronLeft} size={1}/></button>
                    <button className="btn-icon" disabled={!(currentPage < maxPages)} onClick={() => {
                        handlePageChange(true)
                    }}><Icon path={mdiChevronRight} size={1}/></button>
                </div>
            </div>
        </section>);
    }
    return null;
};

export default EvaluationMilieuStage;
