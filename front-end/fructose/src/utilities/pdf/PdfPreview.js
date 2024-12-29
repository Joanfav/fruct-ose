import React, {useState} from "react";
import {Document, Page} from "react-pdf";
import Icon from "@mdi/react";
import {
    mdiChevronLeft,
    mdiChevronRight,
    mdiDownloadOutline,
    mdiFileAlertOutline,
    mdiFullscreen,
    mdiFullscreenExit
} from "@mdi/js";
import {useTranslation} from "react-i18next";


const PdfPreview = ({file, height = 500, filename = 'file.pdf'}) => {
	
	const {t} = useTranslation();
	const [numPages, setNumPages] = useState(null);
	const [pageNumber, setPageNumber] = useState(1);
	const [isFullScreen, setIsFullScreen] = useState(false);
	
	function onDocumentLoadSuccess({numPages}) {
		setNumPages(numPages);
		setPageNumber(1);
	}
	
	function onDocumentLoadError() {
		setNumPages(null);
		setPageNumber(1);
	}
	
	function changePage(offset) {
		setPageNumber(prevPageNumber => prevPageNumber + offset);
	}
	
	function previousPage() {
		changePage(-1);
	}
	
	function nextPage() {
		changePage(1);
	}
	
	function toggleFullScreen() {
		setIsFullScreen(prevState => !prevState);
	}
	
	return (
		<div className={`pdf-file-preview-zone ${isFullScreen ? 'fullscreen' : ''}`} style={{
			height: isFullScreen ? 'auto' : height + 'px',
			width: 'auto',
			overflow: 'hidden',
			overflowX: 'hidden'
		}}>
			<button onClick={toggleFullScreen} className="btn-icon"
			        style={{position: 'absolute', top: '10px', right: '10px', zIndex: 10}}>
				<Icon path={isFullScreen ? mdiFullscreenExit : mdiFullscreen} size={1}/>
			</button>
			<a href={file} download={filename} className="button btn-icon"
			   style={{position: 'absolute', top: '50px', right: '10px', zIndex: 10}}>
				<Icon path={mdiDownloadOutline} size={1}/>
			</a>
			
			<div style={{
				height: isFullScreen ? 'calc(100% - 40px)' : '100%',
				overflowY: isFullScreen ? 'auto' : 'hidden',
				overflowX: 'hidden'
			}}>
				{file && (
					<Document
						file={file}
						onLoadSuccess={onDocumentLoadSuccess}
						onLoadError={onDocumentLoadError}
						loading={<div className="loader-container">
							<div className="loader"></div>
						</div>}
						error={<div className="loader-container text-dark">
							<div><Icon path={mdiFileAlertOutline} size={1.5}/>
								<p>{t("pdf_view_page.error")}</p></div>
						</div>}
					>
						{numPages && pageNumber <= numPages && (
							<Page pageNumber={pageNumber} renderTextLayer={false} renderAnnotationLayer={false}
							      height={isFullScreen ? undefined : height}
							      error={<div className="loader-container text-dark">
								      <div><Icon path={mdiFileAlertOutline} size={1.5}/>
									      <p>{t("pdf_view_page.error")}</p></div>
							      </div>}
							      loading={<div className="loader-container">
								      <div className="loader"></div>
							      </div>}/>
						)}
					</Document>
				)}
			</div>
			
			<div className="pdf-file-preview-navigation">
				<button type="button" className="btn-icon" disabled={pageNumber <= 1} onClick={previousPage}>
					<Icon path={mdiChevronLeft} size={1}/>
				</button>
				<p>
					{pageNumber || (numPages ? 1 : '--')} de {numPages || '--'}
				</p>
				<button type="button" className="btn-icon" disabled={pageNumber >= numPages} onClick={nextPage}>
					<Icon path={mdiChevronRight} size={1}/>
				</button>
			</div>
		</div>
	);
}

export default PdfPreview;