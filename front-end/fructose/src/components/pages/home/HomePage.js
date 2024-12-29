import React from "react";
import HeaderMain from "../../fragments/header/HeaderMain";
import {useTranslation} from "react-i18next";
import '../../../styles/HomePage.css';

const HomePage = () => {
	const {t} = useTranslation();
	return (
		<div className="home-page">
			<HeaderMain/>
			<div className="home-page-content">
				<h1 className="home-page-title">
					{t("home_page.carriere")}<br/>{t("home_page.commence")}<span
					className="home-page-highlight">{t("home_page.ici")}.</span>
				</h1>
			</div>
		</div>
	);
}

export default HomePage;