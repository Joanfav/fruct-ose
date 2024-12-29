import React from "react";
import Icon from "@mdi/react";
import {mdiCheck, mdiClockOutline, mdiClose} from "@mdi/js";

const StatusElement = ({status, text}) => {
	let backgroundColor, iconPath, iconColor;
	
	switch (status) {
		case "pending":
			backgroundColor = "orange";
			iconPath = mdiClockOutline;
			iconColor = "white";
			break;
		case "approved":
			backgroundColor = "green";
			iconPath = mdiCheck;
			iconColor = "white";
			break;
		case "rejected":
			backgroundColor = "red";
			iconPath = mdiClose;
			iconColor = "white";
			break;
		default:
			return null;
	}
	
	return (
		<div style={{
			display: "flex",
			alignItems: "center",
			backgroundColor,
			color: "white",
			padding: "5px 8px",
			borderRadius: "5px",
			fontSize: "16px",
			fontWeight: "bold",
			gap: "5px",
			width: "fit-content",
			cursor: "default"
		}}>
			<Icon path={iconPath} size={0.8} className={`text-${iconColor}`}/>
			<p className="m-0">{text}</p>
		</div>
	);
};

export default StatusElement;