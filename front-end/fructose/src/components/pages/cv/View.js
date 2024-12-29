import React, {useContext} from "react";
import {AuthContext} from "../../providers/AuthProvider";
import {useTranslation} from "react-i18next";

const ViewCV = () => {
	
	const {t} = useTranslation();
	const {currentUser} = useContext(AuthContext);
	
	return (
		<>
			<div className={"dashboard-layout"}>
				<section>
					<h1>{t("view_page.home")}</h1>
					<h5>{t("pdf_view_page.hello")} {(currentUser != null) ? currentUser.fullName :
						<div className={"loading-placeholder"}></div>}
					</h5>
				</section>
			</div>
		</>
	)
}
export default ViewCV