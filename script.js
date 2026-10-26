/* Flavium Sports Portal 2027 — front-end demo. All results are sample data. */
'use strict';

const SPORT_META = {
  cricket: { label: 'Cricket', icon: '🏏', format: 'League' },
  boxcricket: { label: 'Box Cricket', icon: '🏏', format: 'League' },
  football: { label: 'Football', icon: '⚽', format: 'League' },
  basketball: { label: 'Basketball', icon: '🏀', format: 'Knockout' },
  volleyball: { label: 'Volleyball', icon: '🏐', format: 'Knockout' },
  lawntennis: { label: 'Lawn Tennis', icon: '🎾', format: 'Knockout' },
  badminton: { label: 'Badminton', icon: '🏸', format: 'Knockout' },
  tabletennis: { label: 'Table Tennis', icon: '🏓', format: 'Knockout' },
  swimming: { label: 'Swimming', icon: '🏊', format: 'Heats + final' }
};
const SCHOOLS = {
  SPTM: { label: 'SPTM', full: 'School of Pharmacy & Technology Management', programs: [
    { key: 'BPHARM', label: 'B.Pharm', years: [1, 2, 3] },
    { key: 'MBAPHARMA', label: 'MBA Pharma', years: [1, 2, 3, 4] }
  ]},
  MPSTME: { label: 'MPSTME', full: 'Mukesh Patel School of Technology Management & Engineering', programs: [
    { key: 'BTECH', label: 'B.Tech', depts: { AIML: [1,2,3], CE: [1,2,3], CS: [1,2,3], CSDS: [1,2], IT: [1,2,3] } },
    { key: 'MBATECH', label: 'MBA Tech', depts: { CE: [1,2,3] } }
  ]}
};
const VENUES = { cricket:'Main Cricket Ground', boxcricket:'Box Cricket Arena', football:'Football Ground', basketball:'Basketball Court', volleyball:'Volleyball Court', lawntennis:'Lawn Tennis Court', badminton:'Indoor Sports Complex — Badminton Hall', tabletennis:'Indoor Sports Complex — Table Tennis Hall', swimming:'Swimming Pool' };
const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => [...document.querySelectorAll(selector)];
const state = { layout: 'cards', schoolQuickFilter: 'all' };

function teamName(school, program, dept, year, gender) {
  const genderLabel = gender === 'boys' ? 'Boys' : 'Girls';
  const yearLabel = `Year ${year}`;
  if (school === 'SPTM') return `${program === 'BPHARM' ? 'B.Pharm' : 'MBA Pharma'} ${yearLabel} · ${genderLabel}`;
  if (program === 'MBATECH') return `MBA Tech ${dept} ${yearLabel} · ${genderLabel}`;
  return `${dept} ${yearLabel} · ${genderLabel}`;
}
function getTeams(school, gender) {
  const out = [];
  for (const p of SCHOOLS[school].programs) {
    if (p.years) p.years.forEach(year => out.push({ id:`${school}-${p.key}-${year}-${gender==='boys'?'B':'G'}`, name:teamName(school,p.key,null,year,gender), school, gender, program:p.key, dept:null, year }));
    if (p.depts) Object.entries(p.depts).forEach(([dept, years]) => years.forEach(year => out.push({ id:`${school}-${p.key}-${dept}-${year}-${gender==='boys'?'B':'G'}`, name:teamName(school,p.key,dept,year,gender), school, gender, program:p.key, dept, year })));
  }
  return out;
}
function makeFixture({id,sport,school,gender,stage='league',round='Matchday 1',day,slot=1,teams,status='scheduled',score=null,winner=null,note='',event=''}) {
  const starts = ['08:00','10:00','13:00','15:00'];
  const ends = ['10:00','12:00','15:00','17:00'];
  return { id, sport, school, gender, stage, round, date:`2027-01-${String(day).padStart(2,'0')}`, start:starts[slot-1], end:ends[slot-1], venue:VENUES[sport], teams, status, score, winner, note, event, compId:[sport,school,gender,event].join('|') };
}
function generateFixtures() {
  const fixtures = [];
  const addLeague = (sport, school, gender, dayBase, slotBase=1) => {
    const teams = getTeams(school,gender).filter(t => t.program !== 'MBATECH').slice(0,4);
    const pairs = [[0,1],[2,3],[0,2],[1,3],[0,3],[1,2]];
    pairs.forEach(([a,b],i) => {
      const day = dayBase + Math.floor(i/2);
      const score = (day < 18) ? randomScore(sport,i) : null;
      const status = day < 18 ? 'completed' : (day === 18 ? 'in-progress' : 'scheduled');
      const winner = status === 'completed' ? (score.home === score.away ? null : (score.home > score.away ? teams[a].id : teams[b].id)) : null;
      fixtures.push(makeFixture({id:`M${String(fixtures.length+1).padStart(3,'0')}`,sport,school,gender,stage:'league',round:`League — Matchday ${Math.floor(i/2)+1}`,day,slot:((i%2)+slotBase-1)%4+1,teams:[teams[a],teams[b]],status,score:status==='scheduled'?null:score,winner,note:status==='in-progress'?'Sample live score — not an official update.':''}));
    });
    fixtures.push(makeFixture({id:`M${String(fixtures.length+1).padStart(3,'0')}`,sport,school,gender,stage:'final',round:'Final',day:22,slot:4,teams:[],status:'scheduled',note:'Teams will be confirmed after the league table is finalised.'}));
  };
  const addKnockout = (sport,school,gender,dayBase) => {
    const teams=getTeams(school,gender).slice(0,4);
    [[0,3],[1,2]].forEach(([a,b],i)=>{
      const day=dayBase+i;
      const score=day<18?randomScore(sport,i+2):null;
      const status=day<18?'completed':'scheduled';
      const winner=status==='completed'?(score.home>=score.away?teams[a].id:teams[b].id):null;
      fixtures.push(makeFixture({id:`M${String(fixtures.length+1).padStart(3,'0')}`,sport,school,gender,stage:'semi',round:`Semi-final ${i+1}`,day,slot:i+1,teams:[teams[a],teams[b]],status,score,winner}));
    });
    fixtures.push(makeFixture({id:`M${String(fixtures.length+1).padStart(3,'0')}`,sport,school,gender,stage:'final',round:'Final',day:22,slot:4,teams:[],status:'scheduled',note:'Finalists will be confirmed after the semi-finals.'}));
  };
  const randomScore=(sport,i)=>{
    if(sport==='football')return {home:(i+1)%3,away:i%2,detail:'Full time · sample score'};
    if(sport==='cricket'||sport==='boxcricket')return {home:84+i*3,away:72+i*2,detail:'10 overs · sample score'};
    if(sport==='basketball')return {home:42+i*3,away:35+i,detail:'Full time · sample score'};
    if(sport==='volleyball')return {home:2,away:1,detail:'Sets · sample score'};
    return {home:3,away:1,detail:'Team score · sample score'};
  };
  addLeague('cricket','MPSTME','boys',5,1);
  addLeague('boxcricket','SPTM','girls',5,2);
  addLeague('football','MPSTME','boys',8,1);
  ['basketball','volleyball','lawntennis','badminton','tabletennis'].forEach((sport,i)=>{
    ['SPTM','MPSTME'].forEach((school,j)=>['boys','girls'].forEach((gender,k)=>addKnockout(sport,school,gender,6+i+j+k)));
  });
  // Swimming heats and finals, with the programme-specific team categories represented.
  ['SPTM','MPSTME'].forEach((school,si)=>['boys','girls'].forEach((gender,gi)=>['50m Freestyle','4×50m Freestyle Relay'].forEach((event,ei)=>{
    const teams=getTeams(school,gender).slice(0,6);
    const day=10+si+gi+ei;
    [teams.filter((_,i)=>i%2===0),teams.filter((_,i)=>i%2===1)].forEach((heat,hi)=>fixtures.push(makeFixture({id:`M${String(fixtures.length+1).padStart(3,'0')}`,sport:'swimming',school,gender,stage:'heat',round:`Heat ${hi+1}`,event,day:day+hi,slot:hi+1,teams:heat,status:day+hi<18?'completed':'scheduled',score:day+hi<18?{home:'1:02.41',away:'1:04.19',detail:'Sample heat times'}:null,note:'Sample timings only.'})));
    fixtures.push(makeFixture({id:`M${String(fixtures.length+1).padStart(3,'0')}`,sport:'swimming',school,gender,stage:'final',round:'Final',event,day:22,slot:4,teams:[],status:'scheduled',note:'Finalists will be confirmed after the heats.'}));
  })));
  // A couple of schedule exceptions make the status UI easy to preview.
  const postponed = fixtures.find(f=>f.sport==='football'&&f.school==='MPSTME'&&f.stage==='league'&&f.status==='completed');
  if(postponed){postponed.status='postponed';postponed.note='Postponed due to rain (sample). New slot to be announced.';postponed.score=null;postponed.winner=null;}
  const cancelled=fixtures.find(f=>f.sport==='boxcricket'&&f.school==='SPTM'&&f.stage==='league'&&f.status==='completed');
  if(cancelled){cancelled.status='cancelled';cancelled.note='Cancelled — playing area unsafe after rain (sample). No result recorded.';cancelled.score=null;cancelled.winner=null;}
  return fixtures.sort((a,b)=>a.date.localeCompare(b.date)||a.start.localeCompare(b.start)||a.id.localeCompare(b.id));
}
const fixtures = generateFixtures();
const allSports = Object.keys(SPORT_META);

function escapeHtml(value='') { return String(value).replace(/[&<>"']/g,ch=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[ch])); }
function prettyDate(date, includeYear=false) { const d=new Date(`${date}T12:00:00`);return d.toLocaleDateString('en-IN',{day:'numeric',month:'short',...(includeYear?{year:'numeric'}:{})}); }
function prettyStatus(status) { return ({'in-progress':'Live',scheduled:'Scheduled',completed:'Completed',postponed:'Postponed',cancelled:'Cancelled'})[status]||status; }
function getTeamName(team) { return typeof team==='string'?team:(team?.name||'Team to be confirmed'); }
function fixtureTeams(f) { return f.teams?.length?f.teams:[]; }
function renderStats() {
  const counts=fixtures.reduce((acc,f)=>(acc[f.status]=(acc[f.status]||0)+1,acc),{});
  const stats=[['09','Sports listed','Across both schools'],[new Set(fixtures.flatMap(f=>f.teams||[]).map(t=>t.id||t.name)).size,'Teams represented','Across year & department groups'],[counts['completed']||0,'Completed fixtures','Sample results'],[counts['in-progress']||0,'Live now','Demo live state']];
  $('#statsGrid').innerHTML=stats.map(([n,label,note])=>`<article class="stat-card"><div class="stat-number">${n}</div><div class="stat-label">${label}</div><div class="stat-note">${note}</div></article>`).join('');
}
function renderUpcoming() {
  const upcoming=fixtures.filter(f=>['scheduled','in-progress','postponed'].includes(f.status)).slice(0,5);
  $('#upcomingList').innerHTML=upcoming.map(f=>`<div class="compact-fixture" data-fixture="${f.id}" tabindex="0" role="button"><div class="compact-date"><b>${f.date.slice(-2)}</b>${prettyDate(f.date).split(' ')[0]}</div><div class="compact-match"><strong>${escapeHtml(SPORT_META[f.sport].label)} · ${escapeHtml(f.round)}</strong><small>${escapeHtml(f.school)} · ${escapeHtml(f.gender)} · ${escapeHtml(f.venue)}</small></div><span class="compact-state">${prettyStatus(f.status)}</span></div>`).join('')||'<div class="empty-state">No upcoming fixtures in this sample.</div>';
}
function fillSportSelects() {
  const options=allSports.map(s=>`<option value="${s}">${SPORT_META[s].label}</option>`).join('');
  $('#sportFilter').insertAdjacentHTML('beforeend',options);
  $('#standingSport').innerHTML=options;
  $('#standingSport').value='cricket';
}
function getFilteredFixtures() {
  const query=$('#searchFilter').value.trim().toLowerCase();
  const sport=$('#sportFilter').value,school=$('#schoolFilter').value,gender=$('#genderFilter').value,status=$('#statusFilter').value;
  return fixtures.filter(f=>{
    if(sport!=='all'&&f.sport!==sport)return false;
    if(school!=='all'&&f.school!==school)return false;
    if(state.schoolQuickFilter!=='all'&&f.school!==state.schoolQuickFilter)return false;
    if(gender!=='all'&&f.gender!==gender)return false;
    if(status!=='all'&&f.status!==status)return false;
    const text=[f.id,f.round,f.sport,f.school,f.gender,f.venue,f.event,...(f.teams||[]).map(getTeamName)].join(' ').toLowerCase();
    return !query||text.includes(query);
  });
}
function scoreForTeam(f,team) { if(!f.score)return ''; if(typeof f.score.home==='number'&&typeof f.score.away==='number') {const idx=(f.teams||[]).findIndex(t=>t.id===team.id);return idx===0?f.score.home:idx===1?f.score.away:'';}return ''; }
function renderMatchCard(f) {
  const teams=fixtureTeams(f);
  const teamHtml=teams.length?teams.map((t,i)=>{const winner=f.winner===t.id;return `<div class="team-line ${winner?'winner':''}"><span class="team-name">${escapeHtml(getTeamName(t))}</span>${scoreForTeam(f,t)!==''?`<span class="team-score">${escapeHtml(scoreForTeam(f,t))}</span>`:''}${winner?'<span class="winner-tag">WINNER</span>':''}</div>`}).join(''):`<div class="team-line tbd"><span class="team-name">Finalists / participants to be confirmed</span></div>`;
  return `<article class="match-card status-${f.status}" data-fixture="${f.id}" tabindex="0" role="button" aria-label="Details for ${escapeHtml(f.id)}"><div class="match-top"><span class="match-id">${f.id}</span><span class="match-round">${escapeHtml(f.round)}${f.event?' · '+escapeHtml(f.event):''}</span><span class="status-badge ${f.status}">${prettyStatus(f.status)}</span></div><div class="match-body"><div class="teams">${teamHtml}</div><div class="match-meta"><div><span>Sport</span>${SPORT_META[f.sport].label}</div><div><span>School</span>${f.school}</div><div><span>Category</span>${f.gender==='boys'?'Boys':'Girls'}</div><div><span>Time</span>${f.start}–${f.end}</div></div></div><div class="match-foot"><span>⌖ ${escapeHtml(f.venue)}</span><span>◷ ${prettyDate(f.date,true)}</span>${f.note?`<span class="match-note">${escapeHtml(f.note)}</span>`:''}</div></article>`;
}
function renderFixtureTable(list) {
  return `<div class="panel table-panel"><table class="data-table"><thead><tr><th>Match</th><th>Date / time</th><th>Sport</th><th>School / category</th><th>Round</th><th>Status</th></tr></thead><tbody>${list.map(f=>`<tr data-fixture="${f.id}" tabindex="0" role="button"><td><strong>${f.id}</strong><br/>${(f.teams||[]).map(t=>escapeHtml(getTeamName(t))).join(' vs ')||'Participants TBD'}</td><td>${prettyDate(f.date,true)}<br/>${f.start}–${f.end}</td><td>${SPORT_META[f.sport].label}${f.event?'<br/>'+escapeHtml(f.event):''}</td><td>${f.school}<br/>${f.gender}</td><td>${escapeHtml(f.round)}</td><td><span class="status-badge ${f.status}">${prettyStatus(f.status)}</span></td></tr>`).join('')}</tbody></table></div>`;
}
function renderFixtures() {
  const list=getFilteredFixtures();
  $('#fixtureCount').textContent=`${list.length} match${list.length===1?'':'es'}`;
  $('#filterSummary').textContent=`Showing ${list.length} of ${fixtures.length} fixtures${state.schoolQuickFilter!=='all'?' · '+state.schoolQuickFilter:''}`;
  if(!list.length){$('#fixturesContainer').innerHTML='<div class="empty-state"><strong>No fixtures found</strong>Try changing or resetting your filters.</div>';return;}
  if(state.layout==='table'){$('#fixturesContainer').innerHTML=renderFixtureTable(list);return;}
  let currentDate='';let html='';
  list.forEach(f=>{if(f.date!==currentDate){currentDate=f.date;html+=`<div class="date-group">${prettyDate(f.date,true)}</div>`;}html+=renderMatchCard(f);});
  $('#fixturesContainer').innerHTML=html;
}
function calculateStandings(sport,school,gender) {
  const comp=fixtures.filter(f=>f.sport===sport&&f.school===school&&f.gender===gender&&f.stage==='league');
  const map=new Map();
  comp.flatMap(f=>f.teams||[]).forEach(t=>{if(!map.has(t.id))map.set(t.id,{team:t,played:0,w:0,d:0,l:0,for:0,against:0,pts:0});});
  comp.filter(f=>f.status==='completed'&&f.score&&f.teams?.length===2).forEach(f=>{
    const [a,b]=f.teams,ra=map.get(a.id),rb=map.get(b.id);if(!ra||!rb)return;
    const x=Number(f.score.home),y=Number(f.score.away);if(!Number.isFinite(x)||!Number.isFinite(y))return;
    ra.played++;rb.played++;ra.for+=x;ra.against+=y;rb.for+=y;rb.against+=x;
    const rule=sport==='football'?3:2;
    if(x===y){ra.d++;rb.d++;ra.pts++;rb.pts++;}else if(x>y){ra.w++;rb.l++;ra.pts+=rule;}else{rb.w++;ra.l++;rb.pts+=rule;}
  });
  return [...map.values()].sort((a,b)=>b.pts-a.pts||(b.for-b.against)-(a.for-a.against)||b.w-a.w||a.team.name.localeCompare(b.team.name));
}
function renderStandings() {
  const sport=$('#standingSport').value,school=$('#standingSchool').value,gender=$('#standingGender').value;
  const rows=calculateStandings(sport,school,gender);
  if(!['cricket','boxcricket','football'].includes(sport)){$('#standingsContainer').innerHTML='<div class="empty-state"><strong>Knockout format</strong>This sport uses a knockout format, so a league table is not applicable.</div>';return;}
  if(!rows.length){$('#standingsContainer').innerHTML='<div class="empty-state">No league standings available for this selection.</div>';return;}
  $('#standingsContainer').innerHTML=`<table class="data-table"><thead><tr><th>#</th><th>Team</th><th class="num">P</th><th class="num">W</th><th class="num">D</th><th class="num">L</th><th class="num">Diff</th><th class="num">Pts</th></tr></thead><tbody>${rows.map((r,i)=>`<tr><td>${i+1}</td><td>${escapeHtml(r.team.name)}</td><td class="num">${r.played}</td><td class="num">${r.w}</td><td class="num">${r.d}</td><td class="num">${r.l}</td><td class="num">${r.for-r.against}</td><td class="num points">${r.pts}</td></tr>`).join('')}</tbody></table><p class="muted small">Points: ${sport==='football'?'3 for a win, 1 for a draw':'2 for a win, 1 for a draw'}. Sample scores only.</p>`;
}
function renderSports() {
  $('#sportsGrid').innerHTML=allSports.map(s=>{
    const count=fixtures.filter(f=>f.sport===s).length;
    const categories=s==='cricket'?'Boys only':s==='boxcricket'?'Girls only':s==='football'?'Boys only':'Boys & girls';
    return `<article class="sport-card"><h3><span class="sport-icon">${SPORT_META[s].icon}</span>${SPORT_META[s].label}</h3><p>${SPORT_META[s].format} format · ${count} sample fixtures</p><p>Venues: ${escapeHtml(VENUES[s])}</p><div class="sport-chips"><span class="chip">${categories}</span><span class="chip">SPTM</span><span class="chip">MPSTME</span></div><button class="button button-dark" data-sport-jump="${s}">View fixtures →</button></article>`;
  }).join('');
}
function openFixture(id) {
  const f=fixtures.find(x=>x.id===id);if(!f)return;
  const teams=fixtureTeams(f);
  const result=f.score?(f.score.detail||`${f.score.home} – ${f.score.away}`):f.status==='completed'?'Result recorded': 'No result recorded yet';
  $('#modalContent').innerHTML=`<p class="eyebrow">MATCH DETAILS · ${f.id}</p><h2 class="modal-title" id="modalTitle">${escapeHtml(SPORT_META[f.sport].label)} — ${escapeHtml(f.round)}</h2><div class="modal-sub"><span class="status-badge ${f.status}">${prettyStatus(f.status)}</span><span class="chip">${f.school}</span><span class="chip">${f.gender}</span>${f.event?`<span class="chip">${escapeHtml(f.event)}</span>`:''}</div><div class="modal-grid"><div class="modal-fact"><small>Date</small><strong>${prettyDate(f.date,true)}</strong></div><div class="modal-fact"><small>Time</small><strong>${f.start}–${f.end}</strong></div><div class="modal-fact"><small>Venue</small><strong>${escapeHtml(f.venue)}</strong></div><div class="modal-fact"><small>Competition</small><strong>${SPORT_META[f.sport].format} · ${escapeHtml(f.stage)}</strong></div></div><div class="modal-score"><strong>${teams.length?teams.map(t=>escapeHtml(getTeamName(t))).join(' vs '):'Participants to be confirmed'}</strong><p>${escapeHtml(result)}</p></div>${f.note?`<p class="muted small">${escapeHtml(f.note)}</p>`:''}<p class="muted small">This is illustrative demo information. Confirm official fixtures and results with the event organisers.</p>`;
  $('#modalBackdrop').hidden=false;$('#modalClose').focus();
}
function showToast(message){const toast=$('#toast');toast.textContent=message;toast.classList.add('show');clearTimeout(showToast.timer);showToast.timer=setTimeout(()=>toast.classList.remove('show'),2400);}
function jumpTo(view){const map={overview:'overviewSection',fixtures:'fixturesSection',standings:'standingsSection',sports:'sportsSection',about:'aboutSection'};const target=$('#'+map[view]);if(target)target.scrollIntoView({behavior:'smooth',block:'start'});$$('.nav-link').forEach(b=>b.classList.toggle('active',b.dataset.view===view));$('#sidebar').classList.remove('open');}
function resetFilters(){state.schoolQuickFilter='all';$('#searchFilter').value='';$('#sportFilter').value='all';$('#schoolFilter').value='all';$('#genderFilter').value='all';$('#statusFilter').value='all';renderFixtures();}
function init() {
  fillSportSelects();renderStats();renderUpcoming();renderFixtures();renderStandings();renderSports();
  ['searchFilter','sportFilter','schoolFilter','genderFilter','statusFilter'].forEach(id=>$('#'+id).addEventListener(id==='searchFilter'?'input':'change',()=>{state.schoolQuickFilter='all';renderFixtures();}));
  $('#resetFilters').addEventListener('click',resetFilters);
  ['standingSport','standingSchool','standingGender'].forEach(id=>$('#'+id).addEventListener('change',renderStandings));
  $$('.view-btn').forEach(btn=>btn.addEventListener('click',()=>{state.layout=btn.dataset.layout;$$('.view-btn').forEach(b=>b.classList.toggle('active',b===btn));renderFixtures();}));
  document.addEventListener('click',e=>{
    const fixtureEl=e.target.closest('[data-fixture]');if(fixtureEl){openFixture(fixtureEl.dataset.fixture);return;}
    const jump=e.target.closest('[data-jump]');if(jump){jumpTo(jump.dataset.jump);return;}
    const school=e.target.closest('[data-school]');if(school){state.schoolQuickFilter=school.dataset.school;$('#schoolFilter').value=school.dataset.school;renderFixtures();jumpTo('fixtures');showToast(`Showing ${school.dataset.school} fixtures`);return;}
    const sport=e.target.closest('[data-sport-jump]');if(sport){$('#sportFilter').value=sport.dataset.sport;state.schoolQuickFilter='all';renderFixtures();jumpTo('fixtures');}
  });
  document.addEventListener('keydown',e=>{if((e.key==='Enter'||e.key===' ')&&e.target.matches('[data-fixture]')){e.preventDefault();openFixture(e.target.dataset.fixture);}if(e.key==='Escape')$('#modalBackdrop').hidden=true;});
  $('#modalClose').addEventListener('click',()=>$('#modalBackdrop').hidden=true);
  $('#modalBackdrop').addEventListener('click',e=>{if(e.target===$('#modalBackdrop'))$('#modalBackdrop').hidden=true;});
  $$('.nav-link').forEach(btn=>btn.addEventListener('click',()=>jumpTo(btn.dataset.view)));
  $('#menuButton').addEventListener('click',()=>$('#sidebar').classList.toggle('open'));
}
document.addEventListener('DOMContentLoaded',init);
