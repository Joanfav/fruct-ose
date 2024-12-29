import {Link, useNavigate} from "react-router-dom";
import Icon from "@mdi/react";
import {
    mdiChevronLeft,
    mdiChevronRight,
    mdiClockOutline,
    mdiClose,
    mdiFileDocumentOutline, mdiFileQuestionOutline,
} from "@mdi/js";
import React, {useContext, useEffect, useRef, useState} from "react";
import {CvContext} from "../../../providers/CvProvider";
import {useTranslation} from "react-i18next";
import {AuthContext} from "../../../providers/AuthProvider";
import Swal from "sweetalert2";
import Modal from "../../../../utilities/modal/Modal";
import PdfPreview from "../../../../utilities/pdf/PdfPreview";


const CVAdminDashboard = () => {
    const {t} = useTranslation();
    const {getCvContenuById, GetAllCvs, GetCvs} = useContext(CvContext);
    const [allCvs, setAllCvs] = useState(null);
    const [currentCv, setCurrentCV] = useState(null);
    const [currentPageCv, setCurrentPageCv] = useState(1);
    const itemsPerPage = 6;
    const {currentUser, isUserInit, currentToken} = useContext(AuthContext);
    const textareaRef = useRef(null);

    const handleCvClick = (cv) => {
        if (currentCv && currentCv.id === cv.id) {
            setCurrentCV(null);
        } else {
            setCurrentCV(cv);
            fetchCvById(cv.id);
        }
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

    useEffect(() => {
        if (currentUser && isUserInit) {
            (async function () {
                try {
                    const response = await GetAllCvs();
                    const data = await response.text();
                    setAllCvs(JSON.parse(data).filter(cv => !cv.isApproved && !cv.isRefused));
                } catch (error) {
                    console.log("error" + error);
                }
            })();
        }
    }, [currentUser, GetCvs, GetAllCvs, isUserInit]);

    const [currentPage, setCurrentPage] = useState(1);
    const maxPages = (allCvs)?Math.max(1, Math.ceil(allCvs.length / itemsPerPage)):0
    const startIndex = (currentPage - 1) * itemsPerPage;
    const cvsInPage = (allCvs)?allCvs.slice(startIndex, startIndex + itemsPerPage):null;
    const [isRejectModalOpenCv, setRejectModalOpenCv] = useState(false);


    const handleValidateCv = (cvId) => {
        Swal.fire({
            title: t("dashboard_home_page.cv_accept_title"),
            text: t("dashboard_home_page.cv_accept_text"),
            icon: "warning",
            confirmButtonColor: '#ff006c',
            cancelButtonColor: '#fff',
            confirmButtonText: t("dashboard_home_page.cv_accept_confirm_button"),
            cancelButtonText: t("dashboard_home_page.cv_accept_cancel_button")
        }).then((result) => {
            if (result.isConfirmed) {
                fetch(`/accepter-cv/` + cvId, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": currentToken
                    }
                })
                    .then(response => {
                        if (response.ok) {
                            setAllCvs((prevCv) => {
                                const updatedCvs = prevCv.filter((cv) => cv.id !== cvId);
                                if (updatedCvs.length === 0) {
                                    setCurrentCV(null);
                                }
                                return updatedCvs;
                            });
                            setCurrentCV(null);
                            Swal.fire({
                                title: "Success",
                                text: t("dashboard_home_page.cv_accepted_success"),
                                icon: "success",
                                timer: 2000,
                                showConfirmButton: false
                            });
                        } else {
                            throw new Error("Erreur lors de l'acceptation du Cv");
                        }
                    })
                    .catch(error => {
                        console.error("Erreur lors de l'acceptation du cv:", error);
                        Swal.fire({
                            title: "Oops...",
                            text: t("dashboard_home_page.cv_accepted_error"),
                            icon: "error",
                            timer: 2000,
                            showConfirmButton: false
                        });
                    });
            }
        });
    };

    const handleRejectCv = (cvId, string) => {
        fetch(`/refuser-cv/` + cvId, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": currentToken
            },
            body: string,
        })
            .then(response => {
                if (response.ok) {
                    setAllCvs((prevCv) => {
                        const updatedCvs = prevCv.filter((cv) => cv.id !== cvId);
                        if (updatedCvs.length === 0) {
                            setCurrentCV(null);
                        }
                        return updatedCvs;
                    });
                    setCurrentCV(null);
                    Swal.fire({
                        title: "Success",
                        text: t("dashboard_home_page.cv_rejected_success"),
                        icon: "success",
                        timer: 2000,
                        showConfirmButton: false
                    });
                } else {
                    throw new Error("Erreur lors du refus du Cv");
                }
            })
            .catch(error => {
                console.error("Erreur lors du refus du cv:", error);
                Swal.fire({
                    title: "Oops...",
                    text: t("dashboard_home_page.no_comment_reject_cv_text"),
                    icon: "error"
                });
            });
    };

    const fetchCvById = async (cvId) => {
        try {
            const response = await getCvContenuById(cvId);
            const pdfBlob = await response.blob();
            const fileUrl = URL.createObjectURL(pdfBlob);
            const fileSize = pdfBlob.size;
            setCurrentCV((prev) => ({...prev, fileUrl, fileSize}));
        } catch (error) {
            console.error("Erreur lors de la récupération du CV:", error);
        }
    };

    const startIndexCvs = (currentPageCv - 1) * itemsPerPage;
    const endIndexCvs = startIndexCvs + itemsPerPage;

    const GetCVWindow = () => {
        if (currentCv) {
            return (
                <div className="window-frame">
                    <div className="window">
                        <div className="window-titlebar">
                            <h5 className="m-0">{currentCv.filename}</h5>
                            <span className="toolbar-spacer"></span>
                            <button className="btn-icon" onClick={() => setCurrentCV(null)}>
                                <Icon path={mdiClose} size={1}/>
                            </button>
                        </div>
                        <div className="window-content">
                            <div style={{
                                flex: 2,
                                padding: "0 10px",
                            }}>
                                <PdfPreview file={currentCv.fileUrl}/>
                            </div>
                            <br/>
                        </div>
                        <div className="window-options">
                            <div className="toolbar-spacer"></div>
                            <button
                                onClick={() => setRejectModalOpenCv(true)}
                            >
                                {t("modal.reject")}
                            </button>
                            <button
                                className="btn-filled, bg-primary"
                                onClick={() => handleValidateCv(currentCv.id)}
                            >
                                {t("modal.validate")}
                            </button>
                        </div>
                    </div>
                </div>
            )
        }
    }

    if (currentUser) {
        if (currentUser.role === "ADMIN") {
            return (
                <div>
                    {isRejectModalOpenCv && (
                        <Modal onClose={() => setRejectModalOpenCv(false)} onSend={() => {
                            handleRejectCv(currentCv.id, textareaRef.current.value);
                            textareaRef.current.value = "";
                            setRejectModalOpenCv(false);
                        }}>
                            <h4>{t("modal.reject_reason")}</h4>
                            <textarea
                                ref={textareaRef}
                                placeholder={t("modal.reject_reason_placeholder")}
                                style={{
                                    width: "100%",
                                    height: "100px"
                                }}
                            />
                        </Modal>
                    )}
                    {GetCVWindow()}
                    <section>
                        <div className="toolbar-items">
                            <h4 className="m-0 toolbar-spacer">{t("dashboard_home_page.manage_cv")}</h4>
                        </div>
                        <br/>

                        <div className="menu-list">
                            {
                                (!allCvs || allCvs.length === 0) ?
                                    <div className="menu-list-item menu-list-empty-list-placeholder">
                                        {(!allCvs)?
                                            <div className="loader"></div>
                                            :
                                            <div className="no-items-display">
                                                <Icon path={mdiFileQuestionOutline} size={1.4}/>
                                                <h6>{t("dashboard_home_page.no_cv")}</h6>
                                                <p className="text-dark text-mini">{t("discover_offers_page.future_offers")}</p>
                                            </div>}
                                    </div>
                                    :
                                    <>
                                        {cvsInPage.map((cv, index) => (
                                            <div className={"menu-list-item"} key={index}
                                                 onClick={() => handleCvClick(cv)}>
                                                <Icon path={mdiFileDocumentOutline} size={1}/>
                                                <h6 className="m-0">{cv.filename} </h6>
                                                {!cv.isApproved && !cv.isRefused &&
                                                    <Icon path={mdiClockOutline} size={0.7} />}
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

                            <div className="menu-list-item menu-list-footer">
                                <p className="m-0">{(allCvs)?allCvs.length:1} {t("discover_offers_page.results")}</p>
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
                </div>
            )
        }
    }
}
export default CVAdminDashboard;