import {Link} from "react-router-dom";
import Icon from "@mdi/react";
import {
	mdiAccount, mdiAlphaEBoxOutline,
	mdiAlphaFBoxOutline,
	mdiCheck,
	mdiLogout,
	mdiTranslate,
	mdiViewDashboardOutline,
	mdiWeb
} from "@mdi/js";
import React, {useContext, useState} from "react";
import {AuthContext} from "../../providers/AuthProvider";
import {useTranslation} from "react-i18next";

const HeaderMain = ({theme}) => {
	
	const {t, i18n} = useTranslation();
	const [menuOpen, setMenuOpen] = useState(false)
	const [notificationMenuOpen, setNotificationMenuOpen] = useState(false)
	const [languageMenuOpen, setLanguageMenuOpen] = useState(false)
	const {currentUser, SignOutUser} = useContext(AuthContext);
	
	const handleChangeLanguage = (language) => {
		i18n.changeLanguage(language);
		localStorage.setItem('language', language);
	}
	
	const toggleMenu = () => {
		closeAllMenus()
		setMenuOpen(!menuOpen);
	}
	
	const toggleLanguageMenu = () => {
		closeAllMenus()
		setLanguageMenuOpen(!languageMenuOpen);
	}
	
	const closeAllMenus = () => {
		setMenuOpen(false);
		setNotificationMenuOpen(false);
		setLanguageMenuOpen(false)
	}
	
	const isMenuOpen = () => {
		return menuOpen || notificationMenuOpen || languageMenuOpen;
	}
	
	const GetHeaderOptions = () => {
		if (currentUser == null) {
			return (
				<>
					<button onClick={() => {
						toggleLanguageMenu()
					}} className="btn-icon">
						<Icon path={mdiWeb} size={1}/>
					</button>
					
					<Link to="/connexion">
						<button className="btn-icon" style={{marginRight: "10px"}}>
							<Icon path={mdiAccount} size={1}/>
						</button>
					</Link>
					
					<Link to="/creer-utilisateur">
						<button className={"btn-filled"}>{t("header_main_page.signup")}</button>
					</Link>
				</>
			)
		} else {
			return (
				<>
					<div onClick={() => {
						closeAllMenus()
					}} className={"header-user-menu-obstruct"} style={{"display": (isMenuOpen()) ? "block" : "none"}}></div>
					
					<Link to="/dashboard">
						<button className="btn-icon">
							<Icon path={mdiViewDashboardOutline} size={1}/></button>
					</Link>
					
					<button onClick={() => {
						toggleLanguageMenu()
					}} className="btn-icon" style={{marginRight: "10px"}}>
						<Icon path={mdiWeb} size={1}/>
					</button>
					
					<button onClick={() => {
						toggleMenu()
					}} style={{
						"backgroundColor": "black",
						"color": "white",
						"background": "url('/assets/auth/default-profile.jpg') center / cover",
						"width": "42px",
						"height": "42px",
						"borderRadius": "50%",
						"marginRight": "15px"
					}}></button>
					
					<div className={"header-user-menu"} style={{"display": (languageMenuOpen) ? "block" : "none"}}>
						<button className="btn-option" onClick={()=>{handleChangeLanguage('fr')}}>
							<Icon path={mdiAlphaFBoxOutline} size={1}/>
							Français
							<span className="toolbar-spacer"></span>
							{(i18n.language === "fr")?<Icon path={mdiCheck} size={1}/>:null}
						</button>
						<button className="btn-option" onClick={()=>{handleChangeLanguage('en')}}>
							<Icon path={mdiAlphaEBoxOutline} size={1}/>
							English
							<span className="toolbar-spacer"></span>
							{(i18n.language === "en")?<Icon path={mdiCheck} size={1}/>:null}
						</button>
					</div>
					
					<div className={"header-user-menu p-0"} style={{"display": (menuOpen) ? "block" : "none"}}>

						<div className="user-profile-section" style={{height: "100px"}}>
							<div className="user-profile-section-banner" style={{height: "100px"}}>
							</div>
							<div className="user-profile-section-profile-picture centered"
								 style={{backgroundImage: 'url("/assets/auth/default-profile.jpg")'}}>
							</div>
						</div>
						<div style={{padding: "10px"}}>
							<div className={"header-user-menu-profile text-center"} style={{padding: "8px"}}>
								<h6>{currentUser.fullName}</h6>
								<p className={"text-dark"}>{currentUser.email}</p>
							</div>

							<Link to="/dashboard" className="text-decoration-none">
								<button className="btn-option" onClick={()=>{closeAllMenus()}}>
									<Icon path={mdiViewDashboardOutline} size={1} />
									{t("dashboard_home_page.home")}
								</button>
							</Link>

							<button className="btn-option" onClick={() => {
								SignOutUser()
							}}>
								<Icon path={mdiLogout} size={1} />
								{t("header_main_page.disconnect")}
							</button>
						</div>
					</div>
				</>
			)
		}
	}
	
	return (
		<header style={{
			"color": "white",
			background: ((theme && theme === "dark") ? "linear-gradient(rgba(0,0,0,0.7),transparent)" : "none")
		}}>
			<Link to="/"><img src={"/assets/logo/logo.png"} alt="" className={"logo"}/></Link>
			<div className={"toolbar-spacer"}></div>
			{GetHeaderOptions()}
		</header>
	)
}
export default HeaderMain