import React from 'react';
import './App.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import HomePage from './components/pages/home/HomePage';
import CreerUtilisateur from "./components/auth/login-signup/CreerUtilisateur";
import CreerOffreStage from "./components/pages/offre-stage/CreerOffreStage";
import ConnexionUtilisateur from "./components/auth/login-signup/ConnexionUtilisateur";
import Dashboard from "./components/pages/dashboard/Dashboard";
import {AuthProvider} from "./components/providers/AuthProvider";
import {PrivateRoute} from "./utilities/routing/PrivateRoute";
import {RoleRoute} from "./utilities/routing/RoleRoute";
import {CandidatureProvider} from "./components/providers/CandidatureProvider";
import {pdfjs} from "react-pdf";

pdfjs.GlobalWorkerOptions.workerSrc = new URL(
	'pdfjs-dist/build/pdf.worker.min.mjs',
	import.meta.url,
).toString();

function App() {

	return (
		<BrowserRouter>
			<div style={{minHeight: '100vh', position: 'relative'}}>
				<AuthProvider>
					<Routes>
						<Route path="/" element={<HomePage/>}/>
						<Route path="/creer-utilisateur" element={<CreerUtilisateur/>}/>
						<Route path="/connexion" element={<ConnexionUtilisateur/>}/>
						<Route path="/dashboard/*" element={<PrivateRoute element={<Dashboard/>}/>}/>
						<Route path="/creer-offre-stage" element={<RoleRoute element={<CreerOffreStage/>} roles={['ADMIN', 'EMPLOYEUR']}/>}/>
					</Routes>
				</AuthProvider>
			</div>
		</BrowserRouter>
	);
}

export default App;