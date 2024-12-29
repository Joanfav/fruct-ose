import React, {useContext, useEffect, useRef, useState} from "react";
import {
	mdiArrowLeft, mdiCheckCircleOutline, mdiClockOutline,
	mdiClose,
	mdiDeleteOutline,
	mdiDownloadOutline, mdiFileDocumentOutline,
	mdiFileOutline,
	mdiFileQuestionOutline,
	mdiFileUploadOutline
} from "@mdi/js";
import Icon from "@mdi/react";
import {AuthContext} from "../../providers/AuthProvider";
import {Link, useLocation} from "react-router-dom";
import PdfPreview from "../../../utilities/pdf/PdfPreview";
import {CvContext} from "../../providers/CvProvider";
import {useTranslation} from "react-i18next";
import StatusElement from "../../fragments/cv/StatusElement";

const ManageCVs = () => {
	const {t} = useTranslation();
	const {GetCvs, DeleteCv, getCvContenuById} = useContext(CvContext);
	const {isUserInit} = useContext(AuthContext);
	const [cvs, setCvs] = useState(null);
	const [currentContenueCv, setCurrentContenueCv] = useState(null);
	const [currentCv, setCurrentCv] = useState(null);

	const isInitialized = useRef(false);

	const cvId = new URLSearchParams(window.location.search).get("cv");

	useEffect(() => {
		if (isUserInit && cvs === null && !isInitialized.current) {
			(async function () {
				try {
					const response = await GetCvs();
					const data = JSON.parse(await response.text()).reverse();
					setCvs(data);
				} catch (error) {
					console.log(t('manage_cv.upload_error'));
				}
			})();
			isInitialized.current = true;
		}
	}, [isUserInit]);
	
	const fetchCvContenuById = async (cvId) => {
		try {
			const response = await getCvContenuById(cvId);
			const pdfBlob = await response.blob();
			return {fileUrl: URL.createObjectURL(pdfBlob), fileSize: pdfBlob.size}
		} catch (error) {
			console.error("Erreur lors de la récupération du CV:", error);
		}
	};

	useEffect(() => {
		if (cvs && cvs.length > 0 && currentCv === null) {
			(async function () {
				if (cvId) {
					const selectedCv = cvs.find((cv) => cv.id === parseInt(cvId));
					await handleCvSelection(selectedCv)
				} else {
					await handleCvSelection(cvs[0])
				}
			})();
		}
	}, [cvs]);

	const handleCvSelection = async (cv) => {
		let content = await fetchCvContenuById(cv.id);
		setCurrentCv({...cv, fileUrl: content.fileUrl, fileSize: content.fileSize})
	};

	const handleDeleteCv = async (cvId) => {
		try {
			const response = await DeleteCv(cvId);
			if (response.ok) {
				setCvs((prevCvs) => prevCvs.filter((cv) => cv.id !== cvId));
				if (currentContenueCv && currentContenueCv.id === cvId) {
					setCurrentContenueCv(null);
				}
			} else {
				console.error("Erreur lors de la suppression du CV:", response.statusText);
			}
		} catch (error) {
			console.error("Erreur lors de la suppression du CV:", error);
		}
	};

	const getStatutElement = (cv) => {
		if (cv) {
			if (!cv.isApproved && !cv.isRefused) {
				return <Icon path={mdiClockOutline} size={0.7} />;
			} else if (cv.isApproved) {
				return <Icon path={mdiCheckCircleOutline} size={0.7} />;
			} else if (cv.isRefused) {
				return <Icon path={mdiClose} size={0.7} className="text-red" />;
			}
		}
		return null;
	};
	
	const getCvList = () => {
			if (cvs.length > 0) {
				const lastCv = cvs[0];
				return (
					<>
						<div className="toolbar-items">
							<div className="m-0 toolbar-spacer"></div>
							<Link to="../upload-cv">
								<button className="btn-filled">{t('manage_cv.buttons.add')}
									<Icon path={mdiFileUploadOutline} size={1}/></button>
							</Link>
						</div>
						<br/>
						<div className="menu-list">
							<div onClick={() => handleCvSelection(lastCv)}>
								<div
									className={`menu-list-item ${currentCv && lastCv.id === currentCv.id ? "menu-list-item-selected" : ""}  ${((!lastCv.isApproved && !lastCv.isRefused)?"menu-list-item-pending":((lastCv.isRefused)?"menu-list-item-striked":""))}`}
									style={{
										width: "100%",
										display: "flex",
										padding: "0",
										height: "170px",
										boxSizing: "border-box"
									}}>
									<div style={{
										backgroundColor: "rgb(206,206,206)",
										padding: "10px 40px",
										height: "100%",
										boxSizing: "border-box",
										borderRadius: "var(--border-radius) 0 0 var(--border-radius)"
									}}>
										<img src="/assets/dashboard/preview.png" alt="" style={{height: "100%"}}/>
									</div>
									<div style={{paddingLeft: "16px"}}>
										<h4 className="m-0">{lastCv.filename}</h4>
									</div>
									{getStatutElement(lastCv)}
								</div>
							</div>
						</div>
						<br/>
						{cvs.length > 1 && (
							<>
								<div className="menu-list">
									{cvs.slice(1).map((item, index) => (
										<div key={index} onClick={() => handleCvSelection(item)}
											 className={`menu-list-item ${currentCv && item.id === currentCv.id ? "menu-list-item-selected" : ""} ${((!item.isApproved && !item.isRefused)?"menu-list-item-pending":((item.isRefused)?"menu-list-item-striked":""))}`}>
											<Icon path={mdiFileOutline} size={1}/>
												<p className="m-0">{item.filename}</p>
												{getStatutElement(item)}
										</div>
									))}
								</div>
							</>
						)}
					</>
				);
			}

	};

	const DIVISER_KB = 1000;
	const getAppercu = () => {
		if (currentCv) {
				return (
					<div className="dashboard-card" style={{width: "35%"}}>
						<div className="toolbar-items" style={{padding: "10px 10px 10px 16px"}}>
							<h6 className="m-0">{t('manage_cv.titles.preview')}</h6>
							<span className="toolbar-spacer"></span>
							<button className="btn-icon" onClick={() => setCurrentCv(null)}>
								<Icon path={mdiClose} size={1}/>
							</button>
						</div>
						{
							(currentCv.fileUrl && <PdfPreview height={300} file={currentCv.fileUrl}/>)
						}
						<section>
							<div className="toolbar-items" style={{padding: "0 10px"}}>
								<div>
									<h4 className="m-0">{currentCv.filename}</h4>
									<p className="text-dark m-0">{(currentCv.fileSize / DIVISER_KB).toFixed(2)} kb</p>
								</div>
							</div>
							<br/>
							{currentCv.isRefused && currentCv && (
								<p style={{
									color: "red",
									textAlign: "center",
									wordWrap: "break-word"
								}}>{t('manage_cv.messages.reject_reason')}"{currentCv.commentaireRefus}"</p>
							)}
							<a href={currentCv.fileUrl} download={currentCv.filename}
							   className="btn-option">
								<Icon path={mdiDownloadOutline} size={1}/>{t('manage_cv.buttons.download')}
							</a>
							<button className="btn-option" onClick={() => handleDeleteCv(currentCv.id)}>
								<Icon path={mdiDeleteOutline} size={1}/>{t('manage_cv.buttons.delete')}
							</button>
						</section>
					</div>
				);
		}
		return null;
	};
	
	const getCvListSection = () => {
		if(cvs){
			if (cvs.length === 0) {
				return (
					<div className="dashboard-card" style={{width: "65%"}}>
						<div className="dashboard-placeholder-card" style={{backgroundColor: "transparent"}}>
							<div style={{textAlign: "center"}}>
								<Icon path={mdiFileQuestionOutline} size={2}/>
								<h6 style={{margin: "8px 0 14px 0"}}>{t('manage_cv.messages.no_cvs')}</h6>
								<Link to="../upload-cv">
									<button className="btn-filled">{t('manage_cv.buttons.add')}
										<Icon path={mdiFileUploadOutline} size={1}/>
									</button>
								</Link>
							</div>
						</div>
					</div>
				);
			} else {
				return (
					<div className="dashboard-card" style={{width: "65%"}}>
						<section>
							{getCvList()}
						</section>
					</div>
				);
			}
		}
	};
	
	return (
		<>
			<div className="dashboard-card-toolbar">
				<Link to="/dashboard">
					<button className="btn-icon-dashboard">
						<Icon path={mdiArrowLeft} size={1.4}/>
					</button>
				</Link>
				<h1>{t('manage_cv.titles.cvs')}</h1>
			</div>
			<div style={{display: "flex", gap: "20px", alignItems: "start"}}>
				{getCvListSection()}
				{getAppercu()}
			</div>
		</>
	);
};

export default ManageCVs;