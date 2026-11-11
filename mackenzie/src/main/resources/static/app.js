const notice = document.querySelector('#notice');
const views = [...document.querySelectorAll('.view')];
let areas = [];
let vacancies = [];
let currentUser = null;
let csrfToken = null;
let noticeTimer;

function showNotice(message, isError = false) {
    clearTimeout(noticeTimer);
    notice.textContent = message;
    notice.hidden = false;
    notice.classList.toggle('is-error', isError);
    noticeTimer = setTimeout(() => { notice.hidden = true; }, 5000);
}

async function loadCsrfToken() {
    const response = await fetch('/api/auth/csrf', {
        headers: { Accept: 'application/json' },
        credentials: 'same-origin'
    });
    if (!response.ok) throw new Error('Não foi possível iniciar a proteção da sessão.');
    const result = await response.json();
    csrfToken = result.token;
}

async function request(path, options = {}) {
    const headers = { Accept: 'application/json', ...(options.headers || {}) };
    if (options.body !== undefined) headers['Content-Type'] = 'application/json';
    const method = options.method || 'GET';
    if (!['GET', 'HEAD', 'OPTIONS'].includes(method.toUpperCase())) {
        if (!csrfToken) await loadCsrfToken();
        headers['X-XSRF-TOKEN'] = csrfToken;
    }

    const response = await fetch(path, {
        method,
        headers,
        body: options.body === undefined ? undefined : JSON.stringify(options.body),
        credentials: 'same-origin'
    });
    const contentType = response.headers.get('content-type') || '';
    const responseText = response.status === 204 ? '' : await response.text();
    let payload = null;
    if (contentType.includes('application/json') && responseText) {
        try { payload = JSON.parse(responseText); } catch { payload = null; }
    }
    if (!response.ok) {
        const message = payload?.mensagem || (response.status === 401
            ? 'E-mail/senha inválidos ou sessão expirada.'
            : `A solicitação falhou (${response.status}).`);
        throw new Error(message);
    }
    return payload;
}

function escapeHtml(value = '') {
    return String(value).replace(/[&<>"']/g, character => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[character]);
}

function modalityLabel(value) {
    return ({ PRESENCIAL: 'Presencial', REMOTA: 'Remota', HIBRIDA: 'Híbrida' })[value] || value || 'Modalidade não informada';
}

function formatDate(value) {
    if (!value) return '';
    const date = new Date(`${value}T00:00:00`);
    return Number.isNaN(date.valueOf()) ? value : date.toLocaleDateString('pt-BR');
}

async function loadAreas() {
    areas = await request('/api/areas');
    const options = areas.map(area => `<option value="${area.id}">${escapeHtml(area.nome)}</option>`).join('');
    document.querySelector('#areaFilter').innerHTML = '<option value="">Todas as áreas</option>' + options;
    document.querySelector('#jobArea').innerHTML = '<option value="">Selecione uma área</option>' + options;
    document.querySelector('#editAreaSelect').innerHTML = '<option value="">Selecione</option>' + options;
}

function filteredVacancies() {
    const areaId = document.querySelector('#areaFilter').value;
    const modality = document.querySelector('#modalityFilter').value;
    const search = document.querySelector('#searchFilter').value.trim().toLocaleLowerCase('pt-BR');
    return vacancies.filter(vacancy => {
        const matchesArea = !areaId || String(vacancy.area?.id) === areaId;
        const matchesModality = !modality || vacancy.modalidade === modality;
        const searchText = `${vacancy.titulo} ${vacancy.nomeEmpresa} ${vacancy.area?.nome}`.toLocaleLowerCase('pt-BR');
        return matchesArea && matchesModality && (!search || searchText.includes(search));
    });
}

function renderVacancies() {
    const list = document.querySelector('#jobList');
    const visible = filteredVacancies();
    document.querySelector('#jobsCount').textContent = `${visible.length} ${visible.length === 1 ? 'oportunidade' : 'oportunidades'}`;
    document.querySelector('#jobsEmpty').hidden = visible.length > 0;
    list.innerHTML = visible.map(vacancy => `
        <article class="job-row">
            <div>
                <div class="job-meta">
                    <span class="job-company">${escapeHtml(vacancy.nomeEmpresa)}</span>
                    <span class="job-tag">${escapeHtml(vacancy.area?.nome || 'Sem área')}</span>
                    <span>${escapeHtml(modalityLabel(vacancy.modalidade))}</span>
                </div>
                <h2>${escapeHtml(vacancy.titulo)}</h2>
                ${vacancy.descricao ? `<p class="job-description">${escapeHtml(vacancy.descricao)}</p>` : ''}
                <div class="job-details">
                    ${vacancy.localizacao ? `<span>${escapeHtml(vacancy.localizacao)}</span>` : ''}
                    <span>${escapeHtml(vacancy.cargaHorariaSemanal)}h por semana</span>
                </div>
            </div>
            ${currentUser?.perfil === 'ESTUDANTE'
                ? `<div class="job-action"><button class="button button-accent" type="button" data-apply-id="${vacancy.id}">Candidatar-se <span aria-hidden="true">→</span></button></div>`
                : !currentUser
                    ? '<div class="job-action"><button class="button button-outline" type="button" data-login>Entrar para candidatar-se</button></div>'
                    : ''}
        </article>`).join('');
}

async function loadVacancies() {
    const params = new URLSearchParams();
    if (document.querySelector('#areaFilter').value) params.set('areaId', document.querySelector('#areaFilter').value);
    if (document.querySelector('#modalityFilter').value) params.set('modalidade', document.querySelector('#modalityFilter').value);
    const query = params.toString();
    vacancies = await request(`/api/vagas${query ? `?${query}` : ''}`);
    renderVacancies();
}

function switchView(name) {
    views.forEach(view => {
        const isCurrent = view.id === `view-${name}`;
        view.hidden = !isCurrent;
        view.classList.toggle('is-visible', isCurrent);
    });
    document.querySelectorAll('.nav-link').forEach(button => {
        const isCurrent = button.dataset.view === name;
        button.classList.toggle('is-active', isCurrent);
        if (isCurrent) button.setAttribute('aria-current', 'page');
        else button.removeAttribute('aria-current');
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function atualizarInterfaceDaSessao() {
    const autenticado = Boolean(currentUser);
    document.querySelectorAll('.guest-only').forEach(element => { element.hidden = autenticado; });
    document.querySelectorAll('.authenticated-only').forEach(element => { element.hidden = !autenticado; });
    document.querySelectorAll('[data-profile]').forEach(element => {
        element.hidden = !autenticado || element.dataset.profile !== currentUser.perfil;
    });

    if (autenticado) {
        const nomesPerfil = {
            ESTUDANTE: 'Painel do estudante',
            EMPRESA: 'Painel da empresa',
            ADMINISTRADOR: 'Painel administrativo'
        };
        document.querySelector('#sessionIdentity').textContent = `${currentUser.nome} · ${currentUser.perfil.toLocaleLowerCase('pt-BR')}`;
        document.querySelector('#manageTitle').innerHTML = `${nomesPerfil[currentUser.perfil] || 'Meu painel'}<span class="title-period">.</span>`;
        document.querySelector('#panelDescription').textContent = currentUser.perfil === 'ESTUDANTE'
            ? 'Acompanhe suas candidaturas e encontre novas oportunidades.'
            : currentUser.perfil === 'EMPRESA'
                ? 'Publique vagas e acompanhe os candidatos da sua empresa.'
                : 'Gerencie as áreas e consulte os indicadores do portal.';
    }
    renderVacancies();
}

async function iniciarSessao(email, senha) {
    currentUser = await request('/api/auth/login', {
        method: 'POST',
        body: { email, senha }
    });
    csrfToken = null;
    await loadCsrfToken();
    atualizarInterfaceDaSessao();
    switchView('gestao');
}

function formObject(form) {
    return Object.fromEntries(new FormData(form).entries());
}

function renderRecords(records, type) {
    const container = document.querySelector(type === 'applications' ? '#studentResults' : '#companyResults');
    if (!records.length) {
        container.innerHTML = '<p>Nenhum registro encontrado.</p>';
        return;
    }
    if (type === 'applications') {
        container.innerHTML = records.map(record => `
            <div class="record-row"><div class="record-copy"><strong>${escapeHtml(record.tituloVaga)}</strong><span>Inscrição em ${escapeHtml(formatDate(record.dataInscricao))} · candidatura #${record.id}</span></div></div>`).join('');
        return;
    }
    container.innerHTML = records.map(record => `
        <div class="record-row"><div class="record-copy"><strong>${escapeHtml(record.titulo)}</strong><span>#${record.id} · ${escapeHtml(record.situacao)} · ${escapeHtml(record.nomeEmpresa)}</span></div><div class="record-actions"><button class="button button-outline" type="button" data-candidates-id="${record.id}">Ver candidatos</button><button class="button button-danger" type="button" data-close-id="${record.id}" ${record.situacao !== 'ABERTA' ? 'disabled' : ''}>Encerrar</button></div></div>`).join('');
}

function renderCandidates(records, vacancyId) {
    const container = document.querySelector('#companyResults');
    if (!records.length) {
        container.innerHTML = `<p>Nenhuma candidatura para a vaga #${vacancyId}.</p>`;
        return;
    }
    container.innerHTML = `<p>Candidatos da vaga #${vacancyId}</p>` + records.map(record => `
        <div class="record-row"><div class="record-copy"><strong>${escapeHtml(record.nomeEstudante)}</strong><span>${escapeHtml(record.emailEstudante)} · ${escapeHtml(record.curso)}</span></div></div>`).join('');
}

function renderAreas() {
    const options = areas.map(area => `<option value="${area.id}">${escapeHtml(area.nome)}</option>`).join('');
    document.querySelector('#jobArea').innerHTML = '<option value="">Selecione uma área</option>' + options;
    document.querySelector('#editAreaSelect').innerHTML = '<option value="">Selecione</option>' + options;
    document.querySelector('#areaFilter').innerHTML = '<option value="">Todas as áreas</option>' + options;
}

document.querySelector('.brand').addEventListener('click', event => {
    event.preventDefault();
    switchView('oportunidades');
});
document.querySelectorAll('.nav-link').forEach(button => button.addEventListener('click', () => switchView(button.dataset.view)));
document.querySelector('#loginForm').addEventListener('submit', async event => {
    event.preventDefault();
    const form = event.currentTarget;
    const credentials = formObject(form);
    try {
        await iniciarSessao(credentials.email, credentials.senha);
        document.querySelector('#loginResult').textContent = '';
        form.reset();
        showNotice('Sessão iniciada.');
    } catch (error) {
        document.querySelector('#loginResult').textContent = error.message;
    }
});

document.querySelector('#logoutButton').addEventListener('click', async () => {
    try {
        await request('/api/auth/logout', { method: 'POST' });
        currentUser = null;
        csrfToken = null;
        await loadCsrfToken();
        atualizarInterfaceDaSessao();
        switchView('oportunidades');
        showNotice('Você saiu da sua conta.');
    } catch (error) { showNotice(error.message, true); }
});

document.querySelector('#jobList').addEventListener('click', async event => {
    const loginButton = event.target.closest('[data-login]');
    if (loginButton) {
        switchView('login');
        return;
    }
    const button = event.target.closest('[data-apply-id]');
    if (!button) return;
    button.disabled = true;
    try {
        await request(`/api/vagas/${button.dataset.applyId}/candidaturas`, { method: 'POST' });
        showNotice('Candidatura registrada.');
    } catch (error) { showNotice(error.message, true); }
    finally { button.disabled = false; }
});
document.querySelector('#refreshJobs').addEventListener('click', () => loadVacancies().catch(error => showNotice(error.message, true)));
document.querySelector('#areaFilter').addEventListener('change', () => loadVacancies().catch(error => showNotice(error.message, true)));
document.querySelector('#modalityFilter').addEventListener('change', () => loadVacancies().catch(error => showNotice(error.message, true)));
document.querySelector('#searchFilter').addEventListener('input', renderVacancies);
document.querySelector('#clearFilters').addEventListener('click', () => {
    document.querySelector('#areaFilter').value = '';
    document.querySelector('#modalityFilter').value = '';
    document.querySelector('#searchFilter').value = '';
    loadVacancies().catch(error => showNotice(error.message, true));
});

document.querySelectorAll('[data-register]').forEach(button => button.addEventListener('click', () => {
    const isStudent = button.dataset.register === 'estudante';
    document.querySelector('#studentForm').hidden = !isStudent;
    document.querySelector('#companyForm').hidden = isStudent;
    document.querySelectorAll('[data-register]').forEach(option => {
        const selected = option === button;
        option.classList.toggle('is-active', selected);
        option.setAttribute('aria-selected', String(selected));
    });
    document.querySelector('#registrationResult').textContent = '';
}));

document.querySelector('#studentForm').addEventListener('submit', async event => {
    event.preventDefault();
    const form = event.currentTarget;
    try {
        const user = await request('/api/usuarios/estudantes', { method: 'POST', body: formObject(form) });
        const credentials = formObject(form);
        document.querySelector('#registrationResult').textContent = `Conta criada para ${user.nome}.`;
        form.reset();
        await iniciarSessao(credentials.email, credentials.senha);
        showNotice('Estudante cadastrado e sessão iniciada.');
    } catch (error) { showNotice(error.message, true); }
});

document.querySelector('#companyForm').addEventListener('submit', async event => {
    event.preventDefault();
    const form = event.currentTarget;
    try {
        const credentials = formObject(form);
        const user = await request('/api/usuarios/empresas', { method: 'POST', body: credentials });
        document.querySelector('#registrationResult').textContent = `Conta criada para ${user.nome}.`;
        form.reset();
        await iniciarSessao(credentials.email, credentials.senha);
        showNotice('Empresa cadastrada e sessão iniciada.');
    } catch (error) { showNotice(error.message, true); }
});

document.querySelector('#jobForm').addEventListener('submit', async event => {
    event.preventDefault();
    const form = event.currentTarget;
    const data = formObject(form);
    data.areaId = Number(data.areaId);
    data.cargaHorariaSemanal = Number(data.cargaHorariaSemanal);
    try {
        await request('/api/vagas', { method: 'POST', body: data });
        form.reset();
        showNotice('Vaga publicada.');
        vacancies = await request('/api/vagas');
        renderVacancies();
    } catch (error) { showNotice(error.message, true); }
});

document.querySelector('#loadApplications').addEventListener('click', async () => {
    try { renderRecords(await request('/api/candidaturas/minhas'), 'applications'); }
    catch (error) { showNotice(error.message, true); }
});

document.querySelector('#loadCompanyJobs').addEventListener('click', async () => {
    try { renderRecords(await request('/api/vagas/minhas'), 'jobs'); }
    catch (error) { showNotice(error.message, true); }
});

document.querySelector('#companyResults').addEventListener('click', async event => {
    const candidateButton = event.target.closest('[data-candidates-id]');
    const closeButton = event.target.closest('[data-close-id]');
    if (!candidateButton && !closeButton) return;
    try {
        if (candidateButton) {
            const id = candidateButton.dataset.candidatesId;
            renderCandidates(await request(`/api/vagas/${id}/candidaturas`), id);
        } else {
            const id = closeButton.dataset.closeId;
            await request(`/api/vagas/${id}/encerramento`, { method: 'PATCH' });
            showNotice('Vaga encerrada.');
            renderRecords(await request('/api/vagas/minhas'), 'jobs');
        }
    } catch (error) { showNotice(error.message, true); }
});

document.querySelector('#loadReport').addEventListener('click', async () => {
    try {
        const report = await request('/api/relatorios/contagens');
        const items = [
            ['Estudantes', report.estudantes], ['Empresas', report.empresas], ['Vagas abertas', report.vagasAbertas],
            ['Vagas encerradas', report.vagasEncerradas], ['Candidaturas', report.candidaturas]
        ];
        document.querySelector('#reportResults').innerHTML = `<div class="metric-grid">${items.map(([label, count]) => `<div class="metric"><strong>${escapeHtml(count)}</strong><span>${label}</span></div>`).join('')}</div>`;
    } catch (error) { showNotice(error.message, true); }
});

document.querySelector('#loadAreas').addEventListener('click', async () => {
    try { await loadAreas(); showNotice('Áreas atualizadas.'); }
    catch (error) { showNotice(error.message, true); }
});

document.querySelector('#areaForm').addEventListener('submit', async event => {
    event.preventDefault();
    const form = event.currentTarget;
    try {
        await request('/api/areas', { method: 'POST', body: formObject(form) });
        form.reset();
        await loadAreas();
        showNotice('Área adicionada.');
    } catch (error) { showNotice(error.message, true); }
});

document.querySelector('#renameArea').addEventListener('click', async () => {
    const id = document.querySelector('#editAreaSelect').value;
    const nome = document.querySelector('#editAreaName').value.trim();
    if (!id || !nome) { showNotice('Selecione uma área e informe o novo nome.', true); return; }
    try {
        await request(`/api/areas/${id}`, { method: 'PUT', body: { nome } });
        document.querySelector('#editAreaName').value = '';
        await loadAreas();
        showNotice('Área atualizada.');
    } catch (error) { showNotice(error.message, true); }
});

document.querySelector('#deleteArea').addEventListener('click', async () => {
    const id = document.querySelector('#editAreaSelect').value;
    if (!id) { showNotice('Selecione uma área para excluir.', true); return; }
    if (!window.confirm('Excluir a área selecionada?')) return;
    try {
        await request(`/api/areas/${id}`, { method: 'DELETE' });
        await loadAreas();
        showNotice('Área excluída.');
    } catch (error) { showNotice(error.message, true); }
});

async function initialize() {
    try {
        await loadCsrfToken();
        try { currentUser = await request('/api/auth/me'); } catch { currentUser = null; }
        atualizarInterfaceDaSessao();
        await loadAreas();
        await loadVacancies();
    } catch (error) {
        document.querySelector('#jobsCount').textContent = 'API indisponível';
        showNotice(`Não foi possível carregar os dados: ${error.message}`, true);
    }
}

initialize();