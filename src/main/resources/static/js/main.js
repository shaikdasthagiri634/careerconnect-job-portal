// CareerConnect - small effects for the website (no libraries needed)
document.addEventListener("DOMContentLoaded", function () {

    // 1. Fade in every element that has class="reveal" when it scrolls into view
    var items = document.querySelectorAll(".reveal");
    if ("IntersectionObserver" in window) {
        var observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add("visible");
                    observer.unobserve(entry.target);   // animate only once
                }
            });
        }, { threshold: 0.12 });
        items.forEach(function (el) { observer.observe(el); });
    } else {
        items.forEach(function (el) { el.classList.add("visible"); });
    }

    // 2. Count numbers up from 0 (elements with class="count-up", e.g. the stats on the home page)
    document.querySelectorAll(".count-up").forEach(function (el) {
        var target = parseInt(el.textContent, 10);
        if (isNaN(target) || target <= 0) return;
        var current = 0;
        var step = Math.max(1, Math.ceil(target / 40));
        el.textContent = "0";
        var timer = setInterval(function () {
            current = Math.min(target, current + step);
            el.textContent = current;
            if (current >= target) clearInterval(timer);
        }, 30);
    });

    // 3. Darker navbar + "back to top" button after scrolling down
    var nav = document.querySelector(".site-nav");
    var topBtn = document.getElementById("backToTop");
    function onScroll() {
        var down = window.scrollY > 40;
        if (nav) nav.classList.toggle("scrolled", down);
        if (topBtn) topBtn.classList.toggle("show", window.scrollY > 400);
    }
    window.addEventListener("scroll", onScroll);
    onScroll();
    if (topBtn) {
        topBtn.addEventListener("click", function () {
            window.scrollTo({ top: 0, behavior: "smooth" });
        });
    }
});
