let itemsList = document.getElementById('pagination').querySelectorAll('.page-item:not([id])');
let pageItemActive = document.getElementById('pagination').querySelector('.page-item.active');
let pageItemPrev = document.getElementById('pageItemPrev');
let pageItemNext = document.getElementById('pageItemNext');
let maxPage = parseInt(pageItemNext.getAttribute('data-max'));
let currentItem = parseInt(pageItemActive.firstElementChild.textContent)-1;


function nextPageClick() {
    // Enabling previous page
    if (pageItemPrev.matches('.page-item.disabled')) {
        pageItemPrev.setAttribute('class', 'page-item');
    }
    // Showing pages
    if (currentItem < maxPage-2 && currentItem > 1) {
        itemsList[currentItem-1].setAttribute('class', 'page-item visually-hidden');
        itemsList[currentItem+2].setAttribute('class', 'page-item');
        currentItem++;
    } else if (currentItem <= 1) {
        currentItem = 2;
        itemsList[currentItem-2].setAttribute('class', 'page-item visually-hidden');
        itemsList[currentItem+1].setAttribute('class', 'page-item');
    }
    // Disabling next page
    if (currentItem + 2 >= maxPage) {
        pageItemNext.setAttribute('class', 'page-item disabled');
    }
    // Active page
    if (!pageItemActive.matches('.active')) {
        pageItemActive.setAttribute('class', pageItemActive.getAttribute('class') + ' active');
    }
}

function prevPageClick() {
    // Enabling next page
    if (pageItemNext.matches('.page-item.disabled')) {
        pageItemNext.setAttribute('class', 'page-item');
    }
    // Showing pages
    if (currentItem > 1 && currentItem < maxPage-1) {
        itemsList[currentItem-2].setAttribute('class', 'page-item');
        itemsList[currentItem+1].setAttribute('class', 'page-item visually-hidden');
        currentItem--;
    } else if (currentItem == maxPage-1) {
        currentItem = currentItem -2;
        itemsList[currentItem-1].setAttribute('class', 'page-item');
        itemsList[currentItem+2].setAttribute('class', 'page-item visually-hidden');
    }
    // Disabling previous page
    if (currentItem - 1 < 1) {
        pageItemPrev.setAttribute('class', 'page-item disabled');
    }
    // Active page
    if (!pageItemActive.matches('.active')) {
        pageItemActive.setAttribute('class', pageItemActive.getAttribute('class') + ' active');
    }
}