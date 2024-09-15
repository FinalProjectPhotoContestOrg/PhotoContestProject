document.addEventListener('DOMContentLoaded', function() {
    let themeToggleBtn = document.getElementById('theme-toggle');
    let body = document.body;

    const currentTheme = localStorage.getItem('theme');
    if (currentTheme === 'dark') {
        body.classList.add('dark-theme');
        themeToggleBtn.querySelector('#theme-icon').classList.replace('fa-moon', 'fa-sun');
    }

    themeToggleBtn.addEventListener('click', function() {
        let isDarkThemed = body.classList.contains('dark-theme');
        if (!isDarkThemed) {
            body.classList.add('dark-theme');
            themeToggleBtn.querySelector('#theme-icon').classList.replace('fa-moon', 'fa-sun');
            localStorage.setItem('theme', 'dark');
        } else {
            body.classList.remove('dark-theme');
            themeToggleBtn.querySelector('#theme-icon').classList.replace('fa-sun', 'fa-moon');
            localStorage.setItem('theme', 'light');
        }
    });
});