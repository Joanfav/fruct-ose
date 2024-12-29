import React from "react";
import {useTranslation} from "react-i18next";

const Modal = ({children, onClose, onSend}) => {
	const {t} = useTranslation();
	
	return (
		<div className="modal-overlay">
			<div className="modal-content" style={{pointerEvents: "auto"}}>
				{children}
				<div style={{
					display: "flex",
					justifyContent: "flex-end",
					gap: "10px",
					marginTop: "10px"
				}}>
					<button onClick={onClose}>{t("modal.close")}</button>
					<button onClick={onSend} className="btn-filled">{t("modal.send")}</button>
				</div>
			</div>
		</div>
	);
};

export default Modal;