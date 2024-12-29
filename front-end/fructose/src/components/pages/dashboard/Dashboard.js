import React, {useState} from "react";
import HeaderMain from "../../fragments/header/HeaderMain";
import {Route, Routes} from "react-router-dom";
import ManageCVs from "../cv/ManageCVs";
import CreerOffreStage from "../offre-stage/CreerOffreStage";
import DashboardHome from "../home/DashboardHome";
import ViewCV from "../cv/View";
import UploadCV from "../cv/Upload";
import {CvProvider} from "../../providers/CvProvider";
import {AdminProvider} from "../../providers/AdminProvider";
import {RoleRoute} from "../../../utilities/routing/RoleRoute";
import ManageUsers from "../admin/ManageUsers";
import {OffreStageProvider} from "../../providers/OffreStageProvider";
import ModifierOffreStage from "../offre-stage/ModifierOffreStage";
import ManageOffresStage from "../offre-stage/ManageOffresStage";
import DiscoverOffers from "../offre-stage/DiscoverOffers";
import {ApplyOffreWindow} from "../offre-stage/ApplyOffreWindow";
import {CandidatureProvider} from "../../providers/CandidatureProvider";
import {ContratProvider} from "../../providers/ContratProvider";
import {ViewCandidatures} from "../candidatures/ViewCandidatures";
import EvaluationEmployeurEtapes from "../evaluation/EvaluationEmployeurEtapes";
import EvaluationMilieuStageEtape from "../evaluation/EvaluationMilieuStageEtape";

const Dashboard = () => {

	return (
		<>
			<HeaderMain theme={"dark"}></HeaderMain>
			<div className={"dashboard-layout"}>
				<div className="dashboard-head">
				</div>
				<div className="dashboard-content">

					<CvProvider>
						<CandidatureProvider>
						<OffreStageProvider>
							<ContratProvider>
							    <Routes>
                                    <Route path="/manage-cvs" element={<RoleRoute element={<ManageCVs/>} roles={['ETUDIANT']}/>}/>
                                    <Route path="/upload-cv" element={<RoleRoute element={<UploadCV/>} roles={['ETUDIANT']}/>}/>
                                    <Route path="/view-cv" element={<RoleRoute element={<ViewCV/>} roles={['ETUDIANT']}/>}/>
                                    <Route path="/manage-offres-stage" element={<RoleRoute element={<ManageOffresStage/>} roles={['EMPLOYEUR', 'ADMIN']}/>}/>
                                    <Route path="/modifier-offre-stage/:id" element={<RoleRoute element={<ModifierOffreStage/>} roles={['EMPLOYEUR', 'ADMIN']}/>}/>
                                    <Route path="/discover-offers/" element={<RoleRoute element={<CandidatureProvider><ApplyOffreWindow><DiscoverOffers/></ApplyOffreWindow></CandidatureProvider>} roles={['ETUDIANT']}/>}/>
                                    <Route path="/evaluation-step/" element={<EvaluationEmployeurEtapes />} />
									<Route path="/evaluation-milieu-stage-step/" element={<EvaluationMilieuStageEtape />} />
									<Route path="/" element={<DashboardHome/>}/>
							    </Routes>
							</ContratProvider>
						</OffreStageProvider>
						</CandidatureProvider>
						<CandidatureProvider>
							<ContratProvider>
								<Routes>
									<Route path="/view-candidatures" element={<RoleRoute element={<ViewCandidatures/>} roles={['EMPLOYEUR', 'ADMIN']}/>}/>
								</Routes>
							</ContratProvider>
						</CandidatureProvider>
					</CvProvider>

					<Routes>
						<Route path="/creer-offre-stage" element={<CreerOffreStage/>}/>
					</Routes>

					<AdminProvider>
						<Routes>
							<Route path="/admin/manage-users" element={<RoleRoute element={<ManageUsers/>} roles={['ADMIN']}/>}/>
						</Routes>
					</AdminProvider>
				</div>
			</div>
		</>
	)
}

export default Dashboard