const API_URL = 'http://localhost:8081/api/persons';
const PHOTO_URL = 'http://localhost:8081/api/photos';
const app = document.getElementById('app');

// роутинг
function router() {
    const path = window.location.pathname;

    if (path === '/' || path === '/index.html') {
        renderHome();
    } else if (path === '/all') {
        renderAll();
    } else if (path === '/add') {
        renderAddForm();
    } else if (path.startsWith('/edit/')) {
        const id = path.split('/')[2];
        renderEditForm(id);
    }
}

document.addEventListener('click', (e) => {
    if (e.target.matches('[data-link]')) {
        e.preventDefault();
        window.history.pushState({}, '', e.target.href);
        router();
    }
});

// при нажатии назад в браузере
window.addEventListener('popstate', router);


// главная, ближайшие др
async function renderHome() {
    const response = await fetch(`${API_URL}/next`);
    const persons = await response.json();

    if (persons.length === 0) {
        app.innerHTML = `
            <h1>Ближайшие дни рождения</h1>
            <p>В ближайшие 30 дней дней рождения нет</p>
            <a href="/add" data-link>Добавить первого человека</a>
        `;
        return;
    }

    app.innerHTML = `
        <h1>Ближайшие дни рождения</h1>
        <p>В ближайшие 30 дней:</p>
        <ul class="birthday-list">
            ${persons.map(p => {
        const date = new Date(p.birthDate + 'T00:00:00');
        const dateStr = date.toLocaleDateString('ru-RU', {
            day: 'numeric',
            month: 'long'
        });

        let daysText;
        if (p.daysUntilBirthday === 0) {
            daysText = '<strong>СЕГОДНЯ!</strong>';
        } else if (p.daysUntilBirthday === 1) {
            daysText = '<strong>ЗАВТРА</strong>';
        } else {
            daysText = `через ${p.daysUntilBirthday} дней`;
        }

        return `
                    <li class="birthday-item">
                        ${p.photoUrl ?
            `<img src="${PHOTO_URL}/${p.id}" alt="${p.name}" class="person-photo">` :
            `<div class="no-photo">📷</div>`
        }
                        <div class="person-info">
                            <strong>${p.name}</strong><br>
                            ${dateStr} (${daysText}, исполнится ${p.age + 1} лет)
                        </div>
                    </li>
                `;
    }).join('')}
        </ul>
    `;
}

// все люди
async function renderAll() {
    const response = await fetch(API_URL);
    const persons = await response.json();

    app.innerHTML = `
        <h1>Все люди</h1>
        <table>
            <tr>
                <th>Фото</th>
                <th>Имя</th>
                <th>Дата рождения</th>
                <th>Возраст</th>
                <th>Действия</th>
            </tr>
            ${persons.map(p => `
                <tr>
                    <td>
                        ${p.photoUrl ?
        `<img src="${PHOTO_URL}/${p.id}" alt="${p.name}" class="table-photo">` :
        `<span class="no-photo-small">📷</span>`
    }
                    </td>
                    <td>${p.name}</td>
                    <td>${p.birthDate}</td>
                    <td>${p.age}</td>
                    <td>
                        <a href="/edit/${p.id}" data-link>Редактировать</a>
                        <button onclick="deletePerson(${p.id})">Удалить</button>
                    </td>
                </tr>
            `).join('')}
        </table>
    `;
}

// форма добавления
function renderAddForm() {
    app.innerHTML = `
        <h1>Добавить человека</h1>
        <form id="addForm">
            <input type="text" name="name" placeholder="Имя" required><br>
            <input type="date" name="birthDate" required><br>
            
            <label for="photo">Фото (необязательно):</label><br>
            <input type="file" id="photo" name="photo" accept="image/*"><br>
            <div id="photoPreview"></div>
            
            <button type="submit">Сохранить</button>
        </form>
    `;

    // превью фото
    document.getElementById('photo').addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                document.getElementById('photoPreview').innerHTML =
                    `<img src="${e.target.result}" style="max-width:200px;margin-top:10px;">`;
            };
            reader.readAsDataURL(file);
        }
    });

    document.getElementById('addForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);

        // Сначала создаем человека
        const personData = {
            name: formData.get('name'),
            birthDate: formData.get('birthDate')
        };

        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(personData)
        });

        const createdPerson = await response.json();

        // Если есть фото - загружаем его
        const photoFile = formData.get('photo');
        if (photoFile && photoFile.size > 0) {
            const photoFormData = new FormData();
            photoFormData.append('file', photoFile);

            await fetch(`${PHOTO_URL}/${createdPerson.id}`, {
                method: 'POST',
                body: photoFormData
            });
        }

        window.history.pushState({}, '', '/all');
        router();
    });
}

// форма редактирования
async function renderEditForm(id) {
    const response = await fetch(`${API_URL}/${id}`);
    const person = await response.json();

    app.innerHTML = `
        <h1>Редактировать</h1>
        
        ${person.photoUrl ?
        `<div class="current-photo">
                <p>Текущее фото:</p>
                <img src="${PHOTO_URL}/${id}" alt="${person.name}" style="max-width:200px;">
            </div>` :
        '<p>Фото отсутствует</p>'
    }
        
        <form id="editForm">
            <input type="text" name="name" value="${person.name}" required><br>
            <input type="date" name="birthDate" value="${person.birthDate}" required><br>
            
            <label for="photo">Изменить фото (необязательно):</label><br>
            <input type="file" id="photo" name="photo" accept="image/*"><br>
            <div id="photoPreview"></div>
            
            <button type="submit">Сохранить</button>
        </form>
    `;

    // превью нового фото
    document.getElementById('photo').addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                document.getElementById('photoPreview').innerHTML =
                    `<p>Новое фото:</p><img src="${e.target.result}" style="max-width:200px;margin-top:10px;">`;
            };
            reader.readAsDataURL(file);
        }
    });

    document.getElementById('editForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);

        // обновляем данные человека
        const personData = {
            name: formData.get('name'),
            birthDate: formData.get('birthDate')
        };

        await fetch(`${API_URL}/${id}`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(personData)
        });

        // если выбрано новое фото - загружаем его
        const photoFile = formData.get('photo');
        if (photoFile && photoFile.size > 0) {
            const photoFormData = new FormData();
            photoFormData.append('file', photoFile);

            await fetch(`${PHOTO_URL}/${id}`, {
                method: 'POST',
                body: photoFormData
            });
        }

        window.history.pushState({}, '', '/all');
        router();
    });
}

// удаление
async function deletePerson(id) {
    if (confirm('Удалить?')) {
        await fetch(`${API_URL}/${id}`, {method: 'DELETE'});
        router(); // обновить текущую страницу
    }
}

// запуск при загрузке
router();