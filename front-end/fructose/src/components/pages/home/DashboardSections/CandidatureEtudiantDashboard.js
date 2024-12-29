import Icon from "@mdi/react";
import {
	mdiArrowLeft,
	mdiArrowRight,
	mdiBriefcaseOutline,
	mdiBriefcaseRemoveOutline,
	mdiChevronLeft, mdiChevronRight,
	mdiClose
} from "@mdi/js";
import CandidatureProgress from "../../candidatures/CandidatureProgress";
import React, {useContext, useEffect, useRef, useState} from "react";
import {CandidatureContext} from "../../../providers/CandidatureProvider";
import {useTranslation} from "react-i18next";
import {AuthContext} from "../../../providers/AuthProvider";
import CandidatureStatus from "../../candidatures/CandidatureStatus";
import Swal from "sweetalert2";
import {ContratContext} from "../../../providers/ContratProvider";
import {Link} from "react-router-dom";

const CandidatureEtudiantDashboard = () => {

	const {t} = useTranslation();
	const {currentUser, currentToken} = useContext(AuthContext);
	const {candidatures, fetchCandidaturesById, setCandidatures} = useContext(CandidatureContext);
	const [currentCandidature, setCurrentCandidature] = useState(null);

	const sortedCandidatures = [...candidatures].sort((a, b) => {
		const statusOrder = {
			"POSTE_OBTENU": 1,
			"CONTRAT_CREE_PAR_GESTIONNAIRE": 2,
			"ACCEPTE_APRES_ENTREVUE": 3,
			"ENTREVUE_PROPOSE": 4,
			"EN_ATTENTE": 5,
			"ENTREVUE_ACCEPTE_ETUDIANT": 6,
			"ENTREVUE_REFUSE_ETUDIANT": 7,
			"REFUSEE_APRES_ENTREVUE": 8,
			"REFUSEE": 9
		};
		return statusOrder[a.etat] - statusOrder[b.etat];
	});

	const [currentPage, setCurrentPage] = useState(1);
	const itemsPerPage = 5;
	const startIndex = (currentPage - 1) * itemsPerPage;
	const candidaturesInPage = sortedCandidatures.slice(startIndex, startIndex + itemsPerPage);
	const maxPages = Math.max(1, Math.ceil(candidatures.length / itemsPerPage));

	const isInitialized = useRef(false);

	const {
		fetchContratByCandidatureId,
		handleSignerContrat,
		handleRefuseSignerContrat
	} = useContext(ContratContext);

	const [contrat, setContrat] = useState(null);

	const handleSignerContratClick = () => {
		const updatedCandidatures = candidatures.map(item =>
			item.id === currentCandidature.id ? { ...item, needsAttention: false } : item
		);
		setCandidatures(updatedCandidatures)
		handleSignerContrat(contrat, setCurrentCandidature);
	};

	const handleRefuseSignerContratClick = () => {
		handleRefuseSignerContrat(contrat, setCurrentCandidature);
	}

	const fetchContrat = async (id) => {
		try {
			return await fetchContratByCandidatureId(id);
		} catch (error) {
			console.error("Erreur " + error);
		}
	};

	useEffect(() => {
		if (currentCandidature && (currentCandidature.etat === "CONTRAT_CREE_PAR_GESTIONNAIRE" || currentCandidature.etat === "POSTE_OBTENU")) {
			(async function () {
				setContrat(await fetchContrat(currentCandidature.id))
			})();
		}
	}, [currentCandidature]);

	useEffect(() => {
		if (currentUser && !isInitialized.current) {
			(async function () {
				if (currentUser.role === "ETUDIANT") {
					fetchCandidaturesById(currentUser.id);
				}
			})();
			isInitialized.current = true;
		}
		// TODO: Ici il donne WARNING: React Hook useEffect has a missing dependency: 'fetchCandidaturesById'.
		// Mais si le faire il va envoyer 9999 requêtes dans Inspect -> Network
		// It's ok, i'm just a chill guy 👈🐻👉
	}, [currentUser]);

	const isContratsVerifInitialized = useRef(false);

	useEffect(() => {
		if(currentUser && isContratsVerifInitialized.current === false){
			(async function () {
				let tempCandidatures = candidatures
				for (let i = 0; i < tempCandidatures.length; i++){
					isContratsVerifInitialized.current = true;
					let can = tempCandidatures[i];
					if(can.etat === "CONTRAT_CREE_PAR_GESTIONNAIRE"){
						let contrat = await fetchContrat(can.id);
						if(contrat && contrat.signatureEtudiant && contrat.signatureEtudiant === "Non signe"){
							tempCandidatures = tempCandidatures.map(item =>
								item.id === can.id ? { ...item, needsAttention: true } : item
							);
						} else if(contrat && contrat.signatureEmployeur === "Refuse" || contrat.signatureEtudiant === "Refuse"){
							tempCandidatures = tempCandidatures.map(item =>
								item.id === can.id ? { ...item, signRefuse: true } : item
							);
						}
					}
				}
				setCandidatures(tempCandidatures)
			})();
		}
	}, [candidatures]);

	const handleCandidatureClick = (candidature) => {
		setCurrentCandidature(candidature)
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

	const actionIsRequired = (candidature) => {
		return candidature.etat === "ENTREVUE_PROPOSE" || (candidature.needsAttention && candidature.needsAttention === true);
	}

	const GetCandidaturesWindow = () => {
		if (currentCandidature) {
			return (
				<div className="window-frame">
					<div className="window">
						<div className="window-titlebar">
							<h5>{t("dashboard_home_page.application")}</h5>
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
										<h4 className="m-0">{currentCandidature.nomOffre ? currentCandidature.nomOffre : "N/A"}</h4>
										<h6 className="m-0 text-dark">{currentCandidature.compagnie ? currentCandidature.compagnie : "N/A"}</h6>
									</div>
									<Link to={`/dashboard/discover-offers?offer=${currentCandidature.offreStageId}`}>
										<button className="btn-outline">{t("dashboard_home_page.view_offer")}</button>
									</Link>
								</div>
							</section>

							<hr/>

							<CandidatureStatus
								t={t}
								contrat={contrat}
								currentCandidature={currentCandidature}
								handleAcceptInterview={handleAcceptInterview}
								handleRefuseInterview={handleRefuseInterview}
								handleSignerContrat={handleSignerContratClick}
								handleRefuserContrat={handleRefuseSignerContratClick}
							/>

						</div>
					</div>
				</div>
			)
		}
	}

	const handleAcceptInterview = async () => {
		Swal.fire({
			title: t('dashboard_home_page.are_you_sure'),
			html: `${t('dashboard_home_page.proposed_interview_date_employeur')}:<br><strong>${new Date(currentCandidature.dateEntrevue).toLocaleDateString('fr-FR', {
				day: '2-digit',
				month: '2-digit',
				year: 'numeric'
			})}<br></strong> ${t('dashboard_home_page.acceptance_irreversible')}`,
			icon: 'warning',
			showCancelButton: true,
			confirmButtonColor: '#ff006c',
			cancelButtonColor: '#fff',
			confirmButtonText: t('dashboard_home_page.yes_accept'),
			cancelButtonText: t('dashboard_home_page.cancel')
		}).then(async (result) => {
			if (result.isConfirmed) {
				try {
					const response = await fetch(`/candidatures/modifierEtatCandidature/${currentCandidature.id}?nouvelEtat=ENTREVUE_ACCEPTE_ETUDIANT`, {
						method: 'POST',
						headers: {
							'Content-Type': 'application/json',
							'Authorization': currentToken
						}
					});
					if (response.ok) {
						setCurrentCandidature({
							...currentCandidature,
							etat: 'ENTREVUE_ACCEPTE_ETUDIANT'
						});
						setCandidatures(prevCandidatures => prevCandidatures.map(candidature =>
							candidature.id === currentCandidature.id ? {
								...candidature,
								etat: 'ENTREVUE_ACCEPTE_ETUDIANT'
							} : candidature
						));
						Swal.fire({
							title: t('dashboard_home_page.accepted'),
							text: t('dashboard_home_page.interview_accepted'),
							icon: 'success',
							showConfirmButton: false,
							timer: 2000
						})
					} else {
						Swal.fire({
							title: t('dashboard_home_page.error'),
							text: t('dashboard_home_page.interview_refusal_failed_acceptation'),
							icon: 'error'
						});
					}
				} catch (error) {
					Swal.fire({
						title: t('dashboard_home_page.error'),
						text: t('dashboard_home_page.interview_refusal_error_acceptation'),
						icon: 'error'
					});
				}
			}
		});
	};

	const handleRefuseInterview = async () => {
		Swal.fire({
			title: t('dashboard_home_page.are_you_sure'),
			text: t('dashboard_home_page.refuse_interview_warning'),
			icon: 'warning',
			showCancelButton: true,
			confirmButtonColor: '#ff006c',
			cancelButtonColor: '#fff',
			confirmButtonText: t('dashboard_home_page.yes_refuse'),
			cancelButtonText: t('dashboard_home_page.cancel')
		}).then(async (result) => {
			if (result.isConfirmed) {
				try {
					const response = await fetch(`/candidatures/modifierEtatCandidature/${currentCandidature.id}?nouvelEtat=ENTREVUE_REFUSE_ETUDIANT`, {
						method: 'POST',
						headers: {
							'Content-Type': 'application/json',
							'Authorization': currentToken
						}
					});
					if (response.ok) {
						setCurrentCandidature({
							...currentCandidature,
							etat: 'ENTREVUE_REFUSE_ETUDIANT'
						});
						setCandidatures(prevCandidatures => prevCandidatures.map(candidature =>
							candidature.id === currentCandidature.id ? {
								...candidature,
								etat: 'ENTREVUE_REFUSE_ETUDIANT'
							} : candidature
						));
						await Swal.fire({
							title: t('dashboard_home_page.refused'),
							text: t('dashboard_home_page.interview_refused'),
							icon: 'error',
							showConfirmButton: false,
							timer: 2000
						})
					} else {
						await Swal.fire({
							title: t('dashboard_home_page.error'),
							text: t('dashboard_home_page.interview_refusal_failed'),
							icon: 'error'
						});
					}
				} catch (error) {
					console.error("Error refusing interview:", error);
					Swal.fire({
						title: t('dashboard_home_page.error'),
						text: t('dashboard_home_page.interview_refusal_error'),
						icon: 'error'
					});
				}
			}
		});
	};

	if (currentUser) {
		if (currentUser.role === "ETUDIANT") {
			return (
				<div>
					{GetCandidaturesWindow()}
					<section>
						<h4 id="candidatures">{t("dashboard_home_page.my_applications")}</h4>

						<div className="menu-list">
							{
								(candidatures.length === 0)?
									<div className="menu-list-item menu-list-empty-list-placeholder">
										<div className="no-items-display">
											<Icon path={mdiBriefcaseRemoveOutline} size={1.5}/>
											<h6>{t('dashboard_home_page.no_applications')}</h6>
											<p className="text-dark text-mini">{t('dashboard_home_page.find_and_apply')}</p>
										</div>
									</div>
									:
									<>
										{candidaturesInPage.map((candidature, index) => (
											<div className="menu-list-item menu-list-item-64" key={index} onClick={() => handleCandidatureClick(candidature)}>
												<Icon path={mdiBriefcaseOutline} size={1}/>
												<div>
													<h6 className="m-0">{candidature.nomOffre} {(actionIsRequired(candidature)) ?
														<span className="badge bg-orange">Action requise</span> : null}</h6>
													<p className="m-0 text-dark">{candidature.compagnie}</p>
												</div>
												<div className="toolbar-spacer"></div>
												<CandidatureProgress candidature={candidature}></CandidatureProgress>
											</div>
										))}

										{
											(candidaturesInPage.length < itemsPerPage)
												?
												Array.from({length: itemsPerPage - candidaturesInPage.length}, (_, i) => (
													<div key={i} className="menu-list-item menu-list-item-64 menu-list-item-placeholder">
													</div>
												))
												:
												null
										}
									</>
							}

							<div className="menu-list-item menu-list-footer">
								<p className="m-0">{candidatures.length} {t("discover_offers_page.results")}</p>
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

export default CandidatureEtudiantDashboard