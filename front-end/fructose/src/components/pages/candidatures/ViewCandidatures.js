import React, {useContext, useEffect, useRef, useState} from "react";
import {Link} from "react-router-dom";
import Icon from "@mdi/react";
import {
	mdiAccountSchoolOutline,
	mdiArrowLeft,
	mdiBriefcaseCheckOutline,
	mdiBriefcaseRemoveOutline,
	mdiClose,
	mdiFileSign, mdiListBoxOutline,
	mdiPresentation,
	mdiTooltipPlusOutline
} from "@mdi/js";
import {useTranslation} from "react-i18next";
import PdfPreview from "../../../utilities/pdf/PdfPreview";
import {AuthContext} from "../../providers/AuthProvider";
import Modal from "../../../utilities/modal/Modal";
import {CvContext} from "../../providers/CvProvider";
import Swal from "sweetalert2";
import {ContratContext} from "../../providers/ContratProvider";
import CandidatureStatus from "./CandidatureStatus";

const ViewCandidatures = () => {
	const sectionId = new URLSearchParams(window.location.search).get("section");

	const {t} = useTranslation();
	const [currentCandidature, setCurrentCandidature] = useState(null);
	const [candidatures, setCandidatures] = useState([]);
	const [filteredCandidaturesIndexes, setFilteredCandidaturesIndexes] = useState([]);
	const {currentToken} = useContext(AuthContext);
	const {getCvContenuById} = useContext(CvContext)
	const [currentCV, setCurrentCV] = useState(null);
	const [isApproveModalOpen, setApproveModalOpen] = useState(false);
	const interviewDateRef = useRef(null);
	const [candidatureCategory, setCandidatureCategory] = useState((sectionId)?sectionId:"all")
	const [isRejectModalOpen, setRejectModalOpen] = useState(false);
	const textareaRef = useRef(null);
	const {fetchContratByCandidatureId, handleSignerContrat, handleRefuseSignerContrat} = useContext(ContratContext);
	const [contrat, setContrat] = useState(null);
	
	const today = new Date();
	const minDate = new Date(today.setDate(today.getDate() + 3)).toISOString().split('T')[0];
	const maxDate = new Date(today.setMonth(today.getMonth() + 1)).toISOString().split('T')[0];
	
	useEffect(() => {
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
				setCandidatures(data);
			})
			.catch(error => console.error("Error fetching candidatures", error));
	}, [currentToken]);
	
	useEffect(() => {
		loadFilteredCategories(candidatureCategory);
	}, [candidatures, candidatureCategory]);

	useEffect(() => {
		if (currentCandidature && (currentCandidature.etat === "CONTRAT_CREE_PAR_GESTIONNAIRE" || currentCandidature.etat === "POSTE_OBTENU")){
			(async function () {
				try {
					const data = await fetchContratByCandidatureId(currentCandidature.id);
					setContrat(data);
				} catch (error) {
					console.error("Erreur " + error);
				}
			})();
		} else {
			setContrat(null);
		}
	}, [currentCandidature, fetchContratByCandidatureId]);
	
	const fetchCvById = async (cvId) => {
		try {
			const response = await getCvContenuById(cvId);
			const pdfBlob = await response.blob();
			const fileUrl = URL.createObjectURL(pdfBlob);
			const fileSize = pdfBlob.size;
			setCurrentCV({fileUrl: fileUrl, fileSize: fileSize});
			
		} catch (error) {
			console.error("Erreur lors de la récupération du CV:", error);
		}
	};
	
	const handleCandidatureClick = async (candidature, idCv) => {
		await fetchCvById(idCv);
		setCurrentCandidature(candidature);
	};
	
	const handleCategoryChange = (newCategory) => {
		setCandidatureCategory(newCategory)
		loadFilteredCategories(newCategory)
	}

	const handleSignerContratClick = () => {
		handleSignerContrat(contrat, setCurrentCandidature);
	};

	const handleRefuseSignerContratClick = () => {
		handleRefuseSignerContrat(contrat, setCurrentCandidature);
	}
	
	const handleInterviewPassed = async () => {
		Swal.fire({
			title: t('view_candidatures_page.confirm_accept_title'),
			text: t('view_candidatures_page.confirm_accept_text'),
			icon: 'warning',
			showCancelButton: true,
			confirmButtonColor: '#ff006c',
			cancelButtonColor: '#fff',
			confirmButtonText: t('view_candidatures_page.confirm_accept_confirm_button'),
			cancelButtonText: t('view_candidatures_page.confirm_accept_cancel_button')
		}).then(async (result) => {
			if (result.isConfirmed) {
				try {
					const response = await fetch(`/candidatures/modifierEtatCandidature/${currentCandidature.id}?nouvelEtat=ACCEPTE_APRES_ENTREVUE`, {
						method: 'POST',
						headers: {
							'Content-Type': 'application/json',
							'Authorization': currentToken
						}
					});
					
					if (response.ok) {
						const updatedItems = candidatures.map(item =>
							item.candidature.id === currentCandidature.id
								? {
									...item,
									candidature: {
										...item.candidature,
										etat: "ACCEPTE_APRES_ENTREVUE"
									}
								}
								: item
						);
						setCandidatures(updatedItems);
						loadFilteredCategories(candidatureCategory);
						setCurrentCandidature(null);
						await Swal.fire({
							title: t('view_candidatures_page.accepted_title'),
							text: t('view_candidatures_page.accepted_text'),
							icon: 'success',
							showConfirmButton: false,
							timer: 2000
						});
					} else {
						await Swal.fire({
							title: t('view_candidatures_page.error_title'),
							text: t('view_candidatures_page.error_update_text'),
							icon: 'error'
						});
					}
				} catch (error) {
					console.error("Error:", error);
					await Swal.fire({
						title: t('view_candidatures_page.error_title'),
						text: t('view_candidatures_page.error_occurred_text'),
						icon: 'error'
					});
				}
			}
		});
	};
	
	const handleInterviewFail = async () => {
		Swal.fire({
			title: t('view_candidatures_page.confirm_reject_title'),
			text: t('view_candidatures_page.confirm_reject_text'),
			icon: 'warning',
			showCancelButton: true,
			confirmButtonColor: '#ff006c',
			cancelButtonColor: '#fff',
			confirmButtonText: t('view_candidatures_page.confirm_reject_confirm_button'),
			cancelButtonText: t('view_candidatures_page.confirm_reject_cancel_button')
		}).then(async (result) => {
			if (result.isConfirmed) {
				try {
					const response = await fetch(`/candidatures/modifierEtatCandidature/${currentCandidature.id}?nouvelEtat=REFUSEE_APRES_ENTREVUE`, {
						method: 'POST',
						headers: {
							'Content-Type': 'application/json',
							'Authorization': currentToken
						}
					});
					
					if (response.ok) {
						const updatedItems = candidatures.map(item =>
							item.candidature.id === currentCandidature.id
								? {
									...item,
									candidature: {
										...item.candidature,
										etat: "REFUSEE_APRES_ENTREVUE"
									}
								}
								: item
						);
						setCandidatures(updatedItems);
						loadFilteredCategories(candidatureCategory);
						setCurrentCandidature(null);
						await Swal.fire({
							title: t('view_candidatures_page.rejected_title'),
							text: t('view_candidatures_page.rejected_text'),
							icon: 'error',
							showConfirmButton: false,
							timer: 2000
						});
					} else {
						await Swal.fire({
							title: t('view_candidatures_page.error_title'),
							text: t('view_candidatures_page.error_reject_text'),
							icon: 'error'
						});
					}
				} catch (error) {
					console.error("Error:", error);
					await Swal.fire({
						title: t('view_candidatures_page.error_title'),
						text: t('view_candidatures_page.error_occurred_text'),
						icon: 'error'
					});
				}
			}
		});
	};
	
	const loadFilteredCategories = (category) => {
		let newFilteredCandidatures = []
		for (let i = 0; i < candidatures.length; i++) {
			let candidature = candidatures[i]
			switch (category) {
				case "all":
					newFilteredCandidatures.push(i)
					break
				case "nouvelles_candidatures":
					if (candidature.candidature.etat === "EN_ATTENTE") {
						newFilteredCandidatures.push(i)
					}
					break
				case "en_entrevue":
					if (candidature.candidature.etat === "ENTREVUE_PROPOSE" ||
						candidature.candidature.etat === "ENTREVUE_ACCEPTE_ETUDIANT") {
						newFilteredCandidatures.push(i)
					}
					break
				case "en_signature":
					if (candidature.candidature.etat === "ACCEPTE_APRES_ENTREVUE" ||
						candidature.candidature.etat === "CONTRAT_CREE_PAR_GESTIONNAIRE") {
						newFilteredCandidatures.push(i)
					}
					break
				case "accepte_total":
					if (candidature.candidature.etat === "POSTE_OBTENU") {
						newFilteredCandidatures.push(i)
					}
					break
				case "rejete_total":
					if (candidature.candidature.etat === "REFUSEE" ||
						candidature.candidature.etat === "REFUSEE_APRES_ENTREVUE" ||
						candidature.candidature.etat === "ENTREVUE_REFUSE_ETUDIANT") {
						newFilteredCandidatures.push(i)
					}
					break
				default:
					break
			}
		}
		setFilteredCandidaturesIndexes(newFilteredCandidatures)
	}
	
	const handleApprove = () => {
		setApproveModalOpen(true);
	};
	
	const handleApproveSubmit = () => {
		const interviewDate = interviewDateRef.current.value;
		
		if (!interviewDate) {
			Swal.fire({
				icon: 'error',
				title: 'Oops...',
				text: t("view_candidatures_page.sweetalert.error_date_entrevue"),
			});
			return;
		}
		
		fetch(`/candidatures/approuver/${currentCandidature.id}`, {
			method: 'PUT',
			headers: {
				"Content-Type": "application/json",
				"Authorization": currentToken
			},
			body: JSON.stringify({
				dateEntrevue: interviewDate,
				etat: "ENTREVUE_PROPOSE"
			})
		})
			.then(response => {
				if (response.ok) {
					Swal.fire({
						icon: 'success',
						title: t("view_candidatures_page.sweetalert.approved"),
						showConfirmButton: false,
						timer: 2000
					});
					
					const updatedItems = candidatures.map(item => item.candidature.id === currentCandidature.id ? {
						...item,
						candidature: {
							...item.candidature,
							etat: "ENTREVUE_PROPOSE",
							dateEntrevue: interviewDate
						}
					} : item);
					setCandidatures(updatedItems)
					setCurrentCandidature(null);
					setApproveModalOpen(false);
				}
			})
			.catch(error => {
				console.error("Error approving candidature", error);
			});
	};
	
	const handleRefuse = () => {
		setRejectModalOpen(true);
	};
	
	const handleRejectSubmit = () => {
		const commentaireRefus = textareaRef.current.value.trim();
		if (!commentaireRefus) {
			Swal.fire({
				icon: 'error',
				title: 'Oops...',
				text: t("view_candidatures_page.sweetalert.error_commentaire_refus"),
			});
			return;
		}
		
		fetch(`/candidatures/refuser/${currentCandidature.id}?commentaireRefus=${encodeURIComponent(commentaireRefus)}`, {
			method: 'PUT',
			headers: {
				"Content-Type": "application/json",
				"Authorization": currentToken
			}
		})
			.then(response => {
				if (response.ok) {
					setRejectModalOpen(false);
					setCurrentCandidature(null);
					
					Swal.fire({
						icon: 'success',
						title: t("view_candidatures_page.sweetalert.refused"),
						showConfirmButton: false,
						timer: 2000
					});
					
					const updatedItems = candidatures.map(item => item.candidature.id === currentCandidature.id ? {
						...item,
						candidature: {
							...item.candidature,
							etat: "REFUSEE",
							commentaireRefus: commentaireRefus
						}
					} : item);
					setCandidatures(updatedItems)
					setCurrentCandidature(null);
					setRejectModalOpen(false);
				}
			})
			.catch(error => {
				console.error("Error refusing candidature", error);
			});
	};
	
	return (
		<>
			<div className="dashboard-card-toolbar">
				<Link to="/dashboard">
					<button className="btn-icon-dashboard">
						<Icon path={mdiArrowLeft} size={1.4}/>
					</button>
				</Link>
				<h1>{t("view_candidatures_page.title")}</h1>
			</div>
			
			<div style={{display: "flex", gap: "20px"}}>
				<div className="dashboard-card" style={{width: "30%"}}>
					<section>
						<button onClick={() => {
							handleCategoryChange("all")
						}}
								className={"btn-option " + ((candidatureCategory === "all") ? "btn-selected" : "")}>
							<Icon path={mdiListBoxOutline} size={1} />
							{t("view_candidatures_page.categories.all")}
						</button>
						<br/>
						<hr className="m-0"/>
						<br/>
						<button onClick={() => {
							handleCategoryChange("nouvelles_candidatures")
						}}
						        className={"btn-option " + ((candidatureCategory === "nouvelles_candidatures") ? "btn-selected" : "")}>
							<Icon path={mdiTooltipPlusOutline} size={1}/>
							{t("view_candidatures_page.new_applications")}
						</button>
						<button onClick={() => {
							handleCategoryChange("en_entrevue")
						}} className={"btn-option " + ((candidatureCategory === "en_entrevue") ? "btn-selected" : "")}>
							<Icon path={mdiPresentation} size={1}/>
							{t("view_candidatures_page.in_interview")}
						</button>
						<button onClick={() => {
							handleCategoryChange("en_signature")
						}} className={"btn-option " + ((candidatureCategory === "en_signature") ? "btn-selected" : "")}>
							<Icon path={mdiFileSign} size={1}/>
							{t("view_candidatures_page.in_contract_signature")}
						</button>
						<button onClick={() => {
							handleCategoryChange("accepte_total")
						}}
						        className={"btn-option " + ((candidatureCategory === "accepte_total") ? "btn-selected" : "")}>
							<Icon path={mdiBriefcaseCheckOutline} size={1}/>
							{t("view_candidatures_page.accepted_position")}
						</button>
						<button onClick={() => {
							handleCategoryChange("rejete_total")
						}} className={"btn-option " + ((candidatureCategory === "rejete_total") ? "btn-selected" : "")}>
							<Icon path={mdiBriefcaseRemoveOutline} size={1}/>
							{t("view_candidatures_page.rejected_applications")}
						</button>
					</section>
				</div>
				
				<div className="dashboard-card" style={{width: "70%", maxHeight: "550px", overflowY: "auto", height: "80vh"}}>
					<section>
						<h5>{t("view_candidatures_page.categories." + candidatureCategory)}</h5>
						<div className="menu-list">
							{
								(filteredCandidaturesIndexes.length > 0) ?
									filteredCandidaturesIndexes.map(index => (
										<div
											key={candidatures[index].candidature.id}
											className={`menu-list-item`}
											onClick={() => handleCandidatureClick(candidatures[index].candidature, candidatures[index].cvId)}
										>
											<Icon path={mdiAccountSchoolOutline} size={1}/>
											<div>
												<p className="m-0">{candidatures[index].etudiant.fullName}</p>
												<p className="m-0 text-dark">{candidatures[index].candidature.offreStageDTO.nom}</p>
											</div>
											<div className="toolbar-spacer"></div>
											{candidatures[index].candidature.etat === "ENTREVUE_ACCEPTE_ETUDIANT" && (
												<span className="badge bg-orange" style={{marginLeft: "10px"}}>{t("view_candidatures_page.action_needed")}</span>
											)}
											{
												(candidatures[index].candidature.etat === "REFUSEE" || candidatures[index].candidature.etat === "REFUSEE_APRES_ENTREVUE")?
													<Icon path={mdiBriefcaseRemoveOutline} size={1} className="text-dark" />
													:
													(candidatures[index].candidature.etat === "POSTE_OBTENU")?
														<Icon path={mdiBriefcaseCheckOutline} size={1} className="text-dark" />
														:
														(candidatures[index].candidature.etat === "EN_ATTENTE")?
															<Icon path={mdiTooltipPlusOutline} size={1} className="text-dark" />
															:null
											}
										</div>
									))
									:
									<>
										<div className="dashboard-placeholder-card" style={{backgroundColor: "transparent"}}>
											<div>
												<Icon path={mdiBriefcaseRemoveOutline} size={2}/>
												<h4>{t("view_candidatures_page.no_results")}</h4>
												<p className="text-dark">{t("view_candidatures_page.no_results_description")}</p>
											</div>
										</div>
									</>
							}
						</div>
					</section>
				</div>
			</div>
			{
				(currentCandidature !== null) ?
					<div className="window-frame">
						<div className="window">
							
							<div className="window-titlebar">
								<h5>{t("view_candidatures_page.application")}</h5>
								<span className="toolbar-spacer"></span>
								<button className="btn-icon" onClick={() => setCurrentCandidature(null)}>
									<Icon path={mdiClose} size={1}/>
								</button>
							</div>
							
							<div className="window-content">
								<section className="nospace">
									<div className="toolbar-items" style={{gap: "8px"}}>
										<div className="user-profile-section-profile-picture" style={{
											"background": "url('/assets/offers/default-company.png') center / cover",
											width: "52px",
											height: "52px",
											borderRadius: "5px",
											margin: 0
										}}></div>
										<div className="toolbar-spacer">
											<h4 className="m-0">{currentCandidature.offreStageDTO ? currentCandidature.offreStageDTO.nom : "Loading"}</h4>
											<h6 className="m-0 text-dark">{currentCandidature.offreStageDTO ? currentCandidature.offreStageDTO.compagnie : "Loading"}</h6>
										</div>
									</div>
								</section>
								
								<hr/>
								
								<section className="nospace">
									<div style={{
										display: "flex",
										alignItems: "center",
										marginBottom: 15
									}}>
										<h5 style={{margin: 0}}>{t("view_candidatures_page.applicant_profile")} {currentCandidature.etudiantDTO ? currentCandidature.etudiantDTO.fullName : "Loading"} </h5>
									</div>
									{
										(currentCV !== null && currentCV.fileUrl !== null ?
												<PdfPreview height={300} file={currentCV.fileUrl}/>
												: null
										)
									}
								</section>
								
								<hr/>
								
								<CandidatureStatus
									t={t}
									contrat={contrat}
									currentCandidature={currentCandidature}
									handleApprove={handleApprove}
									handleRefuse={handleRefuse}
									handleInterviewPassed={handleInterviewPassed}
									handleInterviewFail={handleInterviewFail}
									handleSignerContrat={handleSignerContratClick}
									handleRefuserContrat={handleRefuseSignerContratClick}
								/>
							</div>
						</div>
					</div>
					: null
			}
			
			{isApproveModalOpen && (
				<Modal onClose={() => setApproveModalOpen(false)} onSend={handleApproveSubmit}>
					<h4>{t("view_candidatures_page.interview_date")}</h4>
					<input type="date" ref={interviewDateRef} min={minDate} max={maxDate} style={{
						width: "100%",
						height: "30px"
					}}/>
				</Modal>
			)}
			
			{isRejectModalOpen && (
				<Modal onClose={() => setRejectModalOpen(false)} onSend={handleRejectSubmit}>
					<h4>{t("modal.reject_reason")}</h4>
					<textarea ref={textareaRef} placeholder={t("modal.reject_reason_placeholder")} style={{
						width: "100%",
						height: "100px"
					}}/>
				</Modal>
			)}
		</>
	);
}

export {ViewCandidatures};