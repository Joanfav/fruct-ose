import React, {useContext} from "react";
import {AuthContext} from "../../providers/AuthProvider";
import {useTranslation} from "react-i18next";
import DashboardHomeAdmin from "../admin/DashboardAdmin";
import OffresStagesDashboard from "./DashboardSections/OffresStagesEtudiantDashboard";
import OffresStagesEmpProfDashboard from "./DashboardSections/OffresStagesEmpProfDashboard";
import CVEtudiantDashboard from "./DashboardSections/CVEtudiantDashboard";
import CandidatureEmployeurDashboard from "./DashboardSections/CandidatureEmployeurDashboard";
import CandidatureEtudiantDashboard from "./DashboardSections/CandidatureEtudiantDashboard";
import {AdminProvider} from "../../providers/AdminProvider";
import SuggestionsDashboard from "./Suggestions";
import EvaluationEmployeur from "../evaluation/EvaluationEmployeur";
import EvaluationMilieuStage from "../evaluation/EvaluationMilieuStage";

const DashboardHome = () => {

	const {t} = useTranslation();
	const {currentUser} = useContext(AuthContext);

	const GetDashboardSections = () => {
		if (currentUser != null) {
			if (currentUser.role === "ETUDIANT") {
				return (
					<>
						<OffresStagesDashboard></OffresStagesDashboard>
						<CandidatureEtudiantDashboard></CandidatureEtudiantDashboard>
						<CVEtudiantDashboard></CVEtudiantDashboard>
					</>
					)
			} else if (currentUser.role === "EMPLOYEUR") {
				return (
					<>
						<OffresStagesEmpProfDashboard></OffresStagesEmpProfDashboard>
						<CandidatureEmployeurDashboard></CandidatureEmployeurDashboard>
                        <EvaluationEmployeur />
                    </>
				)
			} else if (currentUser.role === "PROFESSEUR") {
				return (
						<OffresStagesEmpProfDashboard></OffresStagesEmpProfDashboard>
					)

			} else if (currentUser.role === "ADMIN") {
				return (
					<AdminProvider>
						<DashboardHomeAdmin/>
                        <EvaluationEmployeur />
                        <EvaluationMilieuStage />
					</AdminProvider>
				);
			}
		}
	}

	return (
		<>
			<div className="dashboard-card-titlebar">
				<h1>{t("dashboard_home_page.home")}</h1>
				<h5>{t("dashboard_home_page.hello")} {(currentUser != null) ? currentUser.fullName :
					<div className={"loading-placeholder"}></div>}
				</h5>
			</div>

			<div style={{"display": "flex", "gap": "20px"}}>
				<div style={{"width": "70%"}}>
					<div className="dashboard-card">
						{GetDashboardSections()}
					</div>
				</div>
				<div style={{"width": "30%"}}>
					<div className="dashboard-card">
						<div className="user-profile-section">
							<div className="user-profile-section-banner">
							</div>
							<div className="user-profile-section-profile-picture centered"
							     style={{backgroundImage: 'url("/assets/auth/default-profile.jpg")'}}>
							</div>
						</div>
						<br/>
						<section className="nospace text-center">
							<h4 className="m-0">{(currentUser != null) ? currentUser.fullName :
								<span className={"loading-placeholder"}></span>}
							</h4>
							<p className="m-0 text-dark">{(currentUser != null) ? currentUser.email :
								<span className={"loading-placeholder"}></span>}
							</p>
						</section>
						<hr/>

							<SuggestionsDashboard role={(currentUser)?currentUser.role:null}></SuggestionsDashboard>
						<section className="nospace">
							<p className="text-dark">&copy; 2024 - Fructose</p>
							<p className="text-dark">
								<a href="">Accueil</a> - <a href="">Langue</a> - <a href="">Se deconnecter</a>
							</p>
							<br/>
						</section>
					</div>
				</div>
			</div>
		</>
	);
}

export default DashboardHome;