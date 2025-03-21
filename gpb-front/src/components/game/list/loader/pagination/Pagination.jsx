import React from "react";

import './Pagination.css'

export default function Pagination({ elementAmount, pageSize, page, reloadPage, updateSearchParams }) {
    const totalPages = Math.ceil(elementAmount / pageSize);
    const currentPage = +page;

    // Determine the range of page numbers to show
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);

    const renderPageButton = (pageNumber) => (
        <button
            key={pageNumber}
            className={`pagination-button ${currentPage === pageNumber ? "active" : ""}`}
            onClick={() => onPageClick(pageNumber)}
        >
            {pageNumber}
        </button>
    );

    const onPageClick = (selectedPage) => {
        console.info(selectedPage);
        updateSearchParams("pageNum", selectedPage, 1); // Updates state
        setTimeout(() => reloadPage(), 0); // Delays execution to the next tick
    };


    return (
        <div className="pagination">
            {/* First Page & Previous Page */}
            {currentPage > 1 && (
                <>
                    <button onClick={() => onPageClick(1)}>|&lt;</button>
                    <button onClick={() => onPageClick(currentPage - 1)}>&lt;</button>
                </>
            )}

            {/* Page Number Buttons */}
            {Array.from({ length: endPage - startPage + 1 }, (_, idx) => startPage + idx).map(renderPageButton)}

            {/* Next Page & Last Page */}
            {currentPage < totalPages && (
                <>
                    <button disabled={currentPage === totalPages} onClick={() => onPageClick(currentPage + 1)}>&gt;</button>
                    <button disabled={currentPage === totalPages} onClick={() => onPageClick(totalPages)}>&gt;|</button>
                </>
            )}
        </div>
    );
}
