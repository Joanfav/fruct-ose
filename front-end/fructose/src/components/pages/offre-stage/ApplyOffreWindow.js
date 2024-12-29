import {mdiAlert, mdiClose, mdiMapMarkerOutline, mdiOpenInNew} from "@mdi/js";
import Icon from "@mdi/react";
import React, {createContext, useCallback, useContext, useState} from "react";
import {AuthContext} from "../../providers/AuthProvider";
import {CvContext} from "../../providers/CvProvider";
import {Link} from "react-router-dom";
import {CandidatureContext} from "../../providers/CandidatureProvider";
import {useTranslation} from "react-i18next";

const ApplyOffreWindowContext = createContext(undefined);

const ApplyOffreWindow = ({children}) => {
	
	const {t} = useTranslation();
	const {isUserInit} = useContext(AuthContext);
	const {GetCvs} = useContext(CvContext);
	const {ApplyOffreStage} = useContext(CandidatureContext);
	const [dialogState, setDialogState] = useState({open: false, cvId: null, offre: null, resolved: false});
	const [approvedCvs, setApprovedCvs] = useState(null)
	const [currentCv, setCurrentCv] = useState(null)
	const [enChargement, setEnChargement] = useState(false);
	const [error, setError] = useState("")
	
	const openCandidatureWindow = useCallback((offreStage) => {
		if (isUserInit) {
			(async function () {
				setEnChargement(true)
				const response = await GetCvs();
				const data = await response.text();
				let cvs = JSON.parse(data);
				let approvedCvsList = []
				for (let i = 0; i < cvs.length; i++) {
					if (cvs[i].isApproved === true && cvs[i].isRefused === false) {
						approvedCvsList.push(cvs[i])
						setApprovedCvs(approvedCvsList)
					}
				}
				setApprovedCvs(approvedCvsList.reverse())
				if (approvedCvsList.length > 0) {
					setCurrentCv(approvedCvsList[0])
				}
				setEnChargement(false)
			})();
			return new Promise((resolve) => {
				setDialogState({
					open: true,
					offre: offreStage,
					onConfirm: (cvId) => {
						let newState = {...dialogState, cvId: cvId, open: false, resolved: true}
						resolve(true);
						setDialogState(newState);
					},
					onClose: () => {
						let newState = {...dialogState, open: false, resolved: false}
						resolve(false);
						setDialogState(newState);
					}
				});
			});
		}
	}, [dialogState, isUserInit, GetCvs]);
	
	const handleCompleteSubmission = async () => {
		if (currentCv !== null) {
			setError("")
			setEnChargement(true)
			try {
				const response = await ApplyOffreStage(dialogState.offre.id, currentCv.id)
				if (response.ok) {
					dialogState.onConfirm(currentCv.id)
				} else {
					let text = await response.text()
					setError(text)
				}
			} catch (error) {
				setError(error)
			}
			setEnChargement(false)
		}
	}
	
	const handleCvChange = (cvId) => {
		setCurrentCv(approvedCvs.find((element) => element.id = cvId));
	}
	
	return (
		<ApplyOffreWindowContext.Provider value={{openCandidatureWindow}}>
			{children}
			{(dialogState.open)
				?
				<div className="window-frame">
					<div className="window">
						<div className="window-titlebar">
							<h5>{t("apply_offre_window.send_my_candidature")}</h5>
							<button className="btn-icon" onClick={dialogState.onClose}>
								<Icon path={mdiClose} size={1}/></button>
						</div>
						<div className="window-content">
							{(enChargement) ?
								<div className="loader-container">
									<div className="loader"></div>
								</div>
								:
								<>
									<section className="nospace">
										<div className="toolbar-items" style={{gap: "8px"}}>
											<div className="user-profile-section-profile-picture" style={{
												"background": "url('/assets/offers/default-company.png') center / cover",
												width: "52px",
												height: "52px",
												borderRadius: "5px",
												margin: 0
											}}></div>
											<div>
												<h4 className="m-0">{dialogState.offre.nom}</h4>
												<h6 className="m-0 text-dark">{dialogState.offre.compagnie}</h6>
											</div>
										</div>
									</section>
									<section className="nospace">
										<div className="list-bullet">
											<Icon path={mdiMapMarkerOutline} size={1}/>
											<div style={{padding: "4px 0"}}>
												<h6 className="m-0" style={{marginBottom: "5px"}}>{dialogState.offre.adresse}</h6>
											</div>
										</div>
									</section>
									<section className="nospace">
										<br/>
										<h6 className="m-0">{t("apply_offre_window.cv_to_send")}</h6>
										{
											(approvedCvs !== null) ?
												(approvedCvs.length !== 0) ?
													(currentCv !== null) ?
														<>
															<select name="cv" value={currentCv.value} onChange={(e) => {
																handleCvChange(e.target.value)
															}}>
																{approvedCvs.map((item, index) => (
																	<option key={index} value={item.id}>{item.filename}</option>
																))}
															</select>
															<p className="text-dark">{t("apply_offre_window.note")}</p>
															<p className="text-red">{error}</p>
														
														</>
														: null
													:
													<div className="banner">
														<Icon path={mdiAlert} size={1}/>
														<p>{t("apply_offre_window.no_cv")}
															<Link to="/dashboard/manage-cvs">{t("apply_offre_window.see_cv")}<Icon path={mdiOpenInNew} size={0.5}/></Link>
														</p>
													</div>
												:
												<div className="loader"></div>
										}
									
									</section>
								</>}
						</div>
						<div className="window-options">
							<div className="toolbar-spacer"></div>
							<button onClick={dialogState.onClose}>{t("apply_offre_window.cancel")}</button>
							<button className="btn-filled" disabled={(approvedCvs !== null) ? (approvedCvs.length === 0) : true} onClick={() => {
								handleCompleteSubmission()
							}}>{t("apply_offre_window.send")}
							</button>
						</div>
					</div>
				</div>
				:
				null}
		</ApplyOffreWindowContext.Provider>
	)
}
export {ApplyOffreWindow, ApplyOffreWindowContext};