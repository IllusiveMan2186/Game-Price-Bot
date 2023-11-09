import * as React from 'react'


export default function Pagination(props) {
    const listItems = [];
    let lastPage = Math.ceil(props.elementAmount / props.pageSize);
    let i = 1;
    if (+props.page !== 1) {
        listItems.push(<button onClick={e => props.onPageClick(1)}>|&lt;</button>)
        listItems.push(<button onClick={e => props.onPageClick(+props.page - 1)}>&lt;</button>)
    }
    if (+props.page > 4) {
        i = +props.page - 3;
    }
    let end = lastPage;
    if (lastPage > +props.page + 3) {

        end = +props.page + 3;
    }

    for (; i <= end; i++) {
        let f = i
        listItems.push(<button style={{ backgroundColor: +props.page === i && "rgb(56, 113, 219)" }} onClick={e => props.onPageClick(f)}>{i}</button>)
    }

    if (+props.page < props.elementAmount / props.pageSize) {
        listItems.push(<button onClick={e => props.onPageClick(+props.page + 1)}>&gt;</button>)
        listItems.push(<button onClick={e => props.onPageClick(lastPage)}>&gt;|</button>)
    }


    return listItems;

}