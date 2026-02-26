/*
  script.js
  Implements the exam logic: questions array, timer, rendering, scoring, and result display.
*/

// --- Configuration ---
const TOTAL_TIME_SECONDS = 5 * 60; // 5 minutes

// Questions stored in a JavaScript array. Each question has text, options, and correct index.
const questions = [
  { q: 'Which language is used for styling web pages?', options: ['HTML','JQuery','CSS','XML'], correct: 2 },
  { q: 'Which tag is used to create a hyperlink in HTML?', options: ['<link>','<a>','<href>','<hyper>'], correct: 1 },
  { q: 'Which of the following is a JavaScript framework?', options: ['Django','React','Laravel','Rails'], correct: 1 },
  { q: 'What does CSS stand for?', options: ['Cascading Style Sheets','Computer Style Sheets','Creative Style System','Colorful Style Sheets'], correct: 0 },
  { q: 'Which operator is used to assign a value in JavaScript?', options: ['==','===','=>','='], correct: 3 },
  { q: 'Which HTML element defines the title of a document?', options: ['<meta>','<title>','<head>','<header>'], correct: 1 },
  { q: 'Which method adds a new element at the end of an array in JavaScript?', options: ['pop()','push()','shift()','unshift()'], correct: 1 },
  { q: 'Which symbol is used for comments in JavaScript?', options: ['<!-- -->','//','/* */','Both 2 and 3'], correct: 3 },
  { q: 'Which HTML attribute is used to define inline styles?', options: ['class','style','styles','css'], correct: 1 },
  { q: 'Which of these is NOT a JavaScript data type?', options: ['Undefined','Number','Float','Boolean'], correct: 2 }
];

// --- DOM references ---
const startBtn = document.getElementById('start-btn');
const startScreen = document.getElementById('start-screen');
const quizScreen = document.getElementById('quiz-screen');
const resultScreen = document.getElementById('result-screen');
const questionsContainer = document.getElementById('questions');
const timerEl = document.getElementById('timer');
const submitBtn = document.getElementById('submit-btn');
const scoreEl = document.getElementById('score');
const answersListEl = document.getElementById('answers-list');
const restartBtn = document.getElementById('restart-btn');

let timeLeft = TOTAL_TIME_SECONDS;
let timerInterval = null;

// --- Render quiz questions into the DOM ---
function renderQuestions(){
  questionsContainer.innerHTML = '';
  questions.forEach((item, idx) => {
    const qDiv = document.createElement('div');
    qDiv.className = 'question';

    const title = document.createElement('h3');
    title.className = 'q-title';
    title.textContent = `${idx + 1}. ${item.q}`;
    qDiv.appendChild(title);

    const opts = document.createElement('div');
    opts.className = 'options';
    item.options.forEach((opt, i) => {
      const optDiv = document.createElement('div');
      optDiv.className = 'option';

      const input = document.createElement('input');
      input.type = 'radio';
      input.name = `q${idx}`;
      input.id = `q${idx}_opt${i}`;
      input.value = i;

      const label = document.createElement('label');
      label.htmlFor = input.id;
      label.appendChild(input);
      const span = document.createElement('span');
      span.textContent = opt;
      label.appendChild(span);

      optDiv.appendChild(label);
      opts.appendChild(optDiv);
    });

    qDiv.appendChild(opts);
    questionsContainer.appendChild(qDiv);
  });
}

// --- Timer functions ---
function startTimer(){
  updateTimerDisplay();
  timerInterval = setInterval(() => {
    timeLeft--;
    updateTimerDisplay();
    if(timeLeft <= 0){
      clearInterval(timerInterval);
      autoSubmit();
    }
  }, 1000);
}

function updateTimerDisplay(){
  const m = Math.floor(timeLeft / 60).toString().padStart(2,'0');
  const s = (timeLeft % 60).toString().padStart(2,'0');
  timerEl.textContent = `${m}:${s}`;
}

// --- Submission and scoring ---
function collectAnswers(){
  const answers = [];
  questions.forEach((_, idx) => {
    const selected = document.querySelector(`input[name="q${idx}"]:checked`);
    answers.push(selected ? Number(selected.value) : null);
  });
  return answers;
}

function calculateScore(answers){
  let score = 0;
  answers.forEach((ans, idx) => {
    if(ans === questions[idx].correct) score++;
  });
  return score;
}

function showResults(answers, score){
  // hide quiz and show result screen
  quizScreen.classList.add('hidden');
  resultScreen.classList.remove('hidden');

  scoreEl.textContent = `Score: ${score} / ${questions.length} (${Math.round((score/questions.length)*100)}%)`;

  // Show each question with correct answer and user's answer
  answersListEl.innerHTML = '';
  questions.forEach((q, idx) => {
    const item = document.createElement('div');
    item.className = 'question';

    const title = document.createElement('h4');
    title.textContent = `${idx + 1}. ${q.q}`;
    item.appendChild(title);

    const userAns = answers[idx];
    const userText = userAns === null ? 'No answer' : q.options[userAns];
    const correctText = q.options[q.correct];

    const pUser = document.createElement('p');
    pUser.innerHTML = `Your answer: <span class="${userAns === q.correct ? 'answer-correct' : 'answer-wrong'}">${userText}</span>`;
    item.appendChild(pUser);

    if(userAns !== q.correct){
      const pCorrect = document.createElement('p');
      pCorrect.innerHTML = `Correct answer: <span class="answer-correct">${correctText}</span>`;
      item.appendChild(pCorrect);
    }

    answersListEl.appendChild(item);
  });
}

function submitQuiz(){
  // Prevent double submission
  if(resultScreen.classList.contains('hidden') === false) return;

  clearInterval(timerInterval);
  const answers = collectAnswers();
  const score = calculateScore(answers);
  showResults(answers, score);
}

function autoSubmit(){
  // called when timer reaches 0
  alert('Time is up! Your exam will be submitted automatically.');
  submitQuiz();
}

// --- Event handlers ---
startBtn.addEventListener('click', () => {
  startScreen.classList.add('hidden');
  quizScreen.classList.remove('hidden');
  renderQuestions();
  timeLeft = TOTAL_TIME_SECONDS;
  startTimer();
});

submitBtn.addEventListener('click', (e) => {
  e.preventDefault();
  if(confirm('Are you sure you want to submit the exam?')){
    submitQuiz();
  }
});

restartBtn.addEventListener('click', () => {
  // Reset to start
  resultScreen.classList.add('hidden');
  startScreen.classList.remove('hidden');
  questionsContainer.innerHTML = '';
});

// Safety: show questions on load (not visible until start)
renderQuestions();
