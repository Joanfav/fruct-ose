import React, {useContext} from 'react';
import Icon from '@mdi/react';
import {mdiCheckCircleOutline, mdiClockOutline, mdiCloseCircleOutline} from '@mdi/js';
import {AuthContext} from "../../providers/AuthProvider";
import ViewContrat from "../contrat/ViewContrat";

const CandidatureStatus = ({
	                           contrat,
	                           t,
	                           handleRefuse,
	                           handleApprove,
	                           currentCandidature,
	                           handleInterviewPassed,
	                           handleInterviewFail,
	                           handleAcceptInterview,
	                           handleRefuseInterview,
	                           handleSignerContrat,
	                           handleRefuserContrat
                           }) => {
	
	const {currentUser} = useContext(AuthContext);
	const isEmployeur = currentUser.role === 'EMPLOYEUR';

	const getSectionCandidatureInitiale = () => {
		if(currentCandidature.etat === "EN_ATTENTE"){
			if(isEmployeur){
				return(
					<>
						<p>{t("view_candidatures_page.employeur_candidature_initiale_info")}</p>
						<br/>
						<div style={{gap: "10px"}} className="toolbar-items">
							<button className="btn-outline toolbar-spacer" onClick={handleRefuse}>
								{t("view_candidatures_page.refuse")}
							</button>
							<button className="btn-filled toolbar-spacer bg-primary" onClick={handleApprove}>
								{t("manage_users_page.approve")}
							</button>
						</div>
					</>
				)
			} else {
				return (
					<div className="toolbar-items">
						<Icon path={mdiClockOutline} size={1} className="text-orange"/>
						<p className="text-orange m-0">{t("dashboard_home_page.waiting_employer_response")}</p>
					</div>
				)
			}
		} else if (["ENTREVUE_PROPOSE", "ENTREVUE_ACCEPTE_ETUDIANT", "ENTREVUE_REFUSE_ETUDIANT", "REFUSEE_APRES_ENTREVUE", "ACCEPTE_APRES_ENTREVUE", "CONTRAT_CREE_PAR_GESTIONNAIRE", "POSTE_OBTENU"].includes(currentCandidature.etat)){
			return (
				<div className="toolbar-items">
					<Icon path={mdiCheckCircleOutline} size={1} className="text-green"/>
					<p className="text-green m-0">{t("view_candidatures_page.approved")}</p>
				</div>
			)
		} else {
			return (
				<>
					<div className="toolbar-items">
						<Icon path={mdiCloseCircleOutline} size={1} className="text-red"/>
						<p className="text-red m-0">{t("view_candidatures_page.refused_with_comment")}</p>
					</div>
					<div className="list-bullet">

							<>
								<div className="user-profile-section-profile-picture" style={{
									"background": "url('/assets/auth/default-profile.jpg') center / cover",
									width: "32px",
									height: "32px",
									margin: 0
								}}></div>
								<div>
									<h6 className="m-0">{t("view_candidatures_page.employeur_comment")}</h6>
									<p className="m-0 text-dark">{(currentCandidature.commentaireRefus != "")?currentCandidature.commentaireRefus:t("view_candidatures_page.no_comment")}</p>
								</div>
							</>
					</div>
				</>


			)
		}
	}

	const getSectionEntrevue = () => {
		if(currentCandidature.etat === "REFUSEE"){
			return(
				<div className="toolbar-items">
					<Icon path={mdiCloseCircleOutline} size={1} className="text-dark"/>
					<p className="text-dark m-0">{t("view_candidatures_page.interview_not_planned_for_refused_application")}</p>
				</div>
			)
		} else if(currentCandidature.etat === "ENTREVUE_PROPOSE"){
				if(isEmployeur){
					return (
						<>
							<div className="toolbar-items">
								<Icon path={mdiClockOutline} size={1} className="text-orange"/>
								<p className="text-orange m-0">{t("view_candidatures_page.waiting_for_student_confirmation")}</p>
							</div>
							<br/>
							<p>{t("view_candidatures_page.proposed_interview_date", {date: currentCandidature.dateEntrevue})}</p>
						</>
					)
				} else {
					return (
						<>
							<div className="toolbar-items">
								<Icon path={mdiClockOutline} size={1} className="text-orange"/>
								<p className="text-orange m-0">{t("dashboard_home_page.waiting_interview_acceptance")}</p>
							</div>
							<br/>
							<p>{t("view_candidatures_page.etudiant_entrevue_initiale_info")}</p>
							<br/>
							<p>{t("dashboard_home_page.interview_date")}: {currentCandidature.dateEntrevue}</p>
							<br/>
							<div className="toolbar-items" style={{gap: "10px"}}>
								<button className="btn-outline toolbar-spacer" onClick={handleRefuseInterview}>
									{t("dashboard_home_page.refuse")}
								</button>
								<button className="btn-filled toolbar-spacer" onClick={handleAcceptInterview}>
									{t("dashboard_home_page.accept")}
								</button>
							</div>
						</>
					)
				}
		} else if (["ENTREVUE_ACCEPTE_ETUDIANT", "REFUSEE_APRES_ENTREVUE", "ACCEPTE_APRES_ENTREVUE", "CONTRAT_CREE_PAR_GESTIONNAIRE", "POSTE_OBTENU"].includes(currentCandidature.etat)){
			if (isEmployeur){
				return (
					<>
						<div className="toolbar-items">
							<Icon path={mdiCheckCircleOutline} size={1} className="text-green"/>
							<p className="text-green m-0">{t("view_candidatures_page.interview_accepted_by_student")}</p>
						</div>
						<br/>
						<p>{t("view_candidatures_page.proposed_interview_date", {date: currentCandidature.dateEntrevue})}</p>
					</>
				)
			} else {
				return (
					<>
						<div className="toolbar-items">
							<Icon path={mdiCheckCircleOutline} size={1} className="text-green"/>
							<p className="text-green m-0">{t("dashboard_home_page.interview_accepted_by_student")}</p>
						</div>
						<br/>
						<p>{t("dashboard_home_page.interview_date")}: {currentCandidature.dateEntrevue}</p>
					</>
				)
			}
		} else if (currentCandidature.etat === "ENTREVUE_REFUSE_ETUDIANT"){
			return (
				<>
					<div className="toolbar-items">
						<Icon path={mdiCloseCircleOutline} size={1} className="text-red"/>
						<p className="text-red m-0">{t("view_candidatures_page.interview_refused_by_student")}</p>
					</div>
					<br/>
					<p>{t("view_candidatures_page.proposed_interview_date", {date: currentCandidature.dateEntrevue})}</p>
				</>
			)
		} else {
			return (
				<div className="toolbar-items">
					<Icon path={mdiClockOutline} size={1} className="text-dark"/>
					<p className="text-dark m-0">{t("view_candidatures_page.waiting_for_initial_application")}</p>
				</div>
			)
		}
	}

	const getSectionContrat = () => {
		if(currentCandidature.etat === "REFUSEE"){
			return(
				<div className="toolbar-items">
					<Icon path={mdiCloseCircleOutline} size={1} className="text-dark"/>
					<p className="text-dark m-0">{t("view_candidatures_page.contract_not_signed_for_refused_application")}</p>
				</div>
			)
		} else if(currentCandidature.etat === "ENTREVUE_PROPOSE"){
			return (
				<div className="toolbar-items">
					<Icon path={mdiClockOutline} size={1} className="text-dark"/>
					<p className="text-dark m-0">{t("view_candidatures_page.waiting_for_interview_results")}</p>
				</div>
			)
		} else if (currentCandidature.etat === "ENTREVUE_ACCEPTE_ETUDIANT"){
			if (isEmployeur){
				return (

					<>
						<p>{t("view_candidatures_page.employeur_entrevue_info")}</p>
						<br/>
						<div className="toolbar-items" style={{gap: "10px"}}>
							<button className="btn-outline toolbar-spacer" onClick={handleInterviewFail}>
								{t("view_candidatures_page.refuse")}
							</button>
							<button className="btn-filled toolbar-spacer" onClick={handleInterviewPassed}>
								{t("view_candidatures_page.accept")}
							</button>
						</div>
					</>
				)
			} else {
				return (
					<div className="toolbar-items">
						<Icon path={mdiClockOutline} size={1} className="text-orange"/>
						<p className="text-orange m-0">{t("dashboard_home_page.waiting_interview_results")}</p>
					</div>
				)
			}
		} else if (currentCandidature.etat === "ENTREVUE_REFUSE_ETUDIANT"){
			if (isEmployeur) {
				return (
					<div className="toolbar-items">
						<Icon path={mdiCloseCircleOutline} size={1} className="text-dark"/>
						<p className="text-dark m-0">{t("view_candidatures_page.contract_not_signed_after_interview_refusal_by_student")}</p>
					</div>
				)
			} else {// VUE ETUDIANT
				return (
					<div className="toolbar-items">
						<Icon path={mdiCloseCircleOutline} size={1} className="text-dark"/>
						<p className="text-dark m-0">{t("view_candidatures_page.contract_cannot_be_signed_entrevue_rejected_by_student")}</p>
					</div>
				)
			}
		} else if (currentCandidature.etat === "ACCEPTE_APRES_ENTREVUE"){
			return (
					// VUE EMPLOYEUR ET ETUDIANT
					<>
						<div className="toolbar-items">
							<Icon path={mdiCheckCircleOutline} size={1} className="text-green"/>
							<p className="text-green m-0">
								{/*SI VUE EMPLOYEUR */}
								{isEmployeur ?
									t("view_candidatures_page.student_passed_entrevue")
									: // SI VUE ETUDIANT
									t("view_candidatures_page.you_passed_entrevue")
								}
							</p>
						</div>

						<div className={"toolbar-items"}>
							<Icon path={mdiClockOutline} size={1} className="text-orange"/>
							<p className="text-orange m-0">
								{t("view_candidatures_page.waiting_for_contract_generation")}
							</p>
						</div>
					</>
			)
		} else if (currentCandidature.etat === "REFUSEE_APRES_ENTREVUE"){
			return (
				<div className="toolbar-items">
					<Icon path={mdiCloseCircleOutline} size={1} className="text-red"/>
					<p className="text-red m-0">{t("view_candidatures_page.contract_not_signed_after_interview_refusal")}</p>
				</div>
			)
		} else if (currentCandidature.etat === "CONTRAT_CREE_PAR_GESTIONNAIRE" && contrat){
			return (
				<>
					{/*ETUDIANT A REFUSÉ LE CONTRAT ET EMPLOYEUR N'A PAS ENCORE SIGNÉ*/}
					{contrat.signatureEtudiant === "Non signe" && contrat.signatureEmployeur === "Non signe" && (
						<>
							<div className="toolbar-items">
								<Icon path={mdiClockOutline} size={1} className={"text-blue"}/>
								<p className={"text-blue m-0"}>
									{t("view_candidatures_page.waiting_for_your_signature")}
								</p>
							</div>

							<div className="toolbar-items">
								<Icon path={mdiClockOutline} size={1} className={"text-orange"}/>
								<p className={"text-orange m-0"}>
									{isEmployeur ? t("view_candidatures_page.waiting_for_student_signature") : t("view_candidatures_page.waiting_for_employer_signature")}
								</p>
							</div>
						</>
					)}

					{/*ETUDIANT N'A PAS ENCORE SIGNÉ LE CONTRAT ET EMPLOYEUR A REFUSÉ*/}
					{contrat.signatureEtudiant === "Non signe" && contrat.signatureEmployeur === "Refuse" && (
						// VUE EMPLOYEUR ET ETUDIANT
						<div className="toolbar-items">
							<Icon path={mdiCloseCircleOutline} size={1} className="text-red"/>
							<p className="text-red m-0">
								{t("view_candidatures_page.contract_refused_by_employer")}
							</p>
						</div>
					)}

					{/*ETUDIANT N'A PAS ENCORE SIGNÉ LE CONTRAT ET EMPLOYEUR A SIGNÉ*/}
					{contrat.signatureEtudiant === "Non signe" && contrat.signatureEmployeur === "Signe" && (
						// VUE EMPLOYEUR ET ETUDIANT
						<div className="toolbar-items">
							<Icon path={mdiClockOutline} size={1} className={isEmployeur ? "text-orange" : "text-blue"}/>
							<p className={isEmployeur ? "text-orange m-0" : "text-blue m-0"}>
								{isEmployeur ? t("view_candidatures_page.waiting_for_student_signature") : t("view_candidatures_page.waiting_for_your_signature")}
							</p>
						</div>
					)}

					{/*ETUDIANT A REFUSÉ LE CONTRAT ET EMPLOYEUR N'A PAS ENCORE SIGNÉ*/}
					{contrat.signatureEtudiant === "Refuse" && contrat.signatureEmployeur === "Non signe" && (
						// VUE EMPLOYEUR ET ETUDIANT
						<div className="toolbar-items">
							<Icon path={mdiCloseCircleOutline} size={1} className="text-red"/>
							<p className="text-red m-0">
								{t("view_candidatures_page.contract_refused_by_student")}
							</p>
						</div>
					)}

					{/*ETUDIANT ET EMPLOYEUR ONT REFUSÉ LE CONTRAT*/}
					{contrat.signatureEtudiant === "Refuse" && contrat.signatureEmployeur === "Refuse" && (
						// VUE EMPLOYEUR ET ETUDIANT
						<div className="toolbar-items">
							<Icon path={mdiCloseCircleOutline} size={1} className="text-red"/>
							<p className="text-red m-0">
								{t("view_candidatures_page.contract_refused_by_all")}
							</p>
						</div>
					)}

					{/*ETUDIANT A REFUSÉ LE CONTRAT ET EMPLOYEUR A SIGNÉ*/}
					{contrat.signatureEtudiant === "Refuse" && contrat.signatureEmployeur === "Signe" && (
						// VUE EMPLOYEUR ET ETUDIANT
						<div className="toolbar-items">
							<Icon path={mdiCloseCircleOutline} size={1} className="text-red"/>
							<p className="text-red m-0">
								{t("view_candidatures_page.contract_refused_by_student")}
							</p>
						</div>
					)}

					{/*ETUDIANT A SIGNÉ LE CONTRAT ET EMPLOYEUR N'A PAS ENCORE SIGNÉ*/}
					{contrat.signatureEtudiant === "Signe" && contrat.signatureEmployeur === "Non signe" && (
						<>
							<div className="toolbar-items">
								<Icon path={mdiCheckCircleOutline} size={1} className="text-green"/>
								<p className="text-green m-0">{t("view_candidatures_page.contract_signed_by_student")}</p>
							</div>

							<div className="toolbar-items">
								<Icon path={mdiClockOutline} size={1} className={isEmployeur ? "text-orange" : "text-blue"}/>
								<p className={isEmployeur ? "text-orange m-0" : "text-blue m-0"}>
									{isEmployeur ? t("view_candidatures_page.waiting_for_your_signature") : t("view_candidatures_page.waiting_for_employer_signature")}
								</p>
							</div>
						</>
					)}

					{/*ETUDIANT A SIGNÉ LE CONTRAT ET EMPLOYEUR A REFUSÉ*/}
					{contrat.signatureEtudiant === "Signe" && contrat.signatureEmployeur === "Refuse" && (
						// VUE EMPLOYEUR ET ETUDIANT
						<div className="toolbar-items">
							<Icon path={mdiCloseCircleOutline} size={1} className="text-red"/>
							<p className="text-red m-0">{t("view_candidatures_page.contract_refused_by_employer")}</p>
						</div>
					)}

					{/*TOUS ONT SIGNÉ LE CONTRAT*/}
					{contrat.signatureEtudiant === "Signe" && contrat.signatureEmployeur === "Signe" && (
						// VUE EMPLOYEUR ET ETUDIANT
						<div className="toolbar-items">
							<Icon path={mdiCheckCircleOutline} size={1} className="text-green"/>
							<p className="text-green m-0">{t("view_candidatures_page.contract_signed_by_all")}</p>
						</div>
					)}

					<br/>

					{/* PDF DU CONTRAT*/}
					<div>
						{contrat && <ViewContrat contrat={contrat} currentCandidature={currentCandidature} handleSign={handleSignerContrat} handleNoSign={handleRefuserContrat}/>}
					</div>
				</>
			)
		} else {
			return (
				<div className="toolbar-items">
					<Icon path={mdiClockOutline} size={1} className="text-dark"/>
					<p className="text-dark m-0">{t("view_candidatures_page.waiting_for_initial_application")}</p>
				</div>
			)
		}
	}

	if(currentCandidature.etat === "POSTE_OBTENU"){
		return (
			<section>
				<div className="toolbar-items" style={{alignItems: "flex-start"}}>
					<Icon path={mdiCheckCircleOutline} size={2} className="text-green"/>
					<div>
						<h3 className="m-0 text-green">
							{t("view_candidatures_page.titre_poste_obtenu")}
						</h3>
						{(!isEmployeur)?
							<p>
								{t("view_candidatures_page.poste_obtenu")}
							</p>
							:null}
						{contrat && <ViewContrat contrat={contrat} currentCandidature={currentCandidature} handleSign={handleSignerContrat} handleNoSign={handleRefuserContrat}/>}
					</div>
				</div>
				<br/>
			</section>
		)
	} else {
		return (
			<div>
				{/* SECTION CANDIDATURE INITIALE */}
				<section className="nospace">
					<h5>{t("view_candidatures_page.initial_application")}</h5>
					{getSectionCandidatureInitiale()}
				</section>
				<hr/>

				{/* SECTION ENTREVUE */}
				<section className="nospace">
					<h5>{t("view_candidatures_page.interview")}</h5>
					{getSectionEntrevue()}
				</section>
				<hr/>
				{/* SECTION CONTRAT */}
				<section className="nospace">
					<h5>{t("view_candidatures_page.contract")}</h5>
					{getSectionContrat()}
					<br></br>
				</section>
			</div>
		)
	}
}

export default CandidatureStatus;