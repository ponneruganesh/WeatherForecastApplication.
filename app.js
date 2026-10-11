const form = document.getElementById('searchForm');
const input = document.getElementById('cityInput');
const message = document.getElementById('message');
const content = document.getElementById('weatherContent');
const codeInfo = code => {
  if (code === 0) return ['Clear sky', '☀️'];
  if ([1,2].includes(code)) return ['Mainly clear', '🌤️'];
  if (code === 3) return ['Overcast', '☁️'];
  if ([45,48].includes(code)) return ['Foggy', '🌫️'];
  if ([51,53,55,56,57].includes(code)) return ['Drizzle', '🌦️'];
  if ([61,63,65,66,67,80,81,82].includes(code)) return ['Rainy', '🌧️'];
  if ([71,73,75,77,85,86].includes(code)) return ['Snowfall', '❄️'];
  if ([95,96,99].includes(code)) return ['Thunderstorm', '⛈️'];
  return ['Partly cloudy', '🌤️'];
};
const dayName = (date, index) => index === 0 ? 'Today' : new Date(date + 'T12:00:00').toLocaleDateString('en-IN', { weekday: 'short' });
const timeOnly = value => value ? value.slice(-5) : '--:--';
async function loadWeather(city) {
  message.className = 'message'; message.textContent = 'Finding the latest forecast for ' + city + '…'; content.classList.add('hidden');
  try {
    const response = await fetch('/api/weather?city=' + encodeURIComponent(city));
    const data = await response.json();
    if (!response.ok) throw new Error(data.error || 'Unable to get weather data.');
    const now = data.current; const info = codeInfo(now.weatherCode);
    document.getElementById('location').textContent = data.city + (data.country ? ', ' + data.country : '');
    document.getElementById('localTime').textContent = 'Local time · ' + (now.time ? now.time.replace('T', ' ') : data.timezone);
    document.getElementById('condition').textContent = info[0];
    document.getElementById('temperature').textContent = Math.round(now.temperature);
    document.getElementById('feelsLike').textContent = Math.round(now.feelsLike) + '°C';
    document.getElementById('humidity').textContent = now.humidity + '%';
    document.getElementById('wind').textContent = Math.round(now.windSpeed) + ' km/h';
    document.getElementById('rain').textContent = now.precipitation + ' mm';
    document.getElementById('weatherEmoji').textContent = info[1];
    const forecast = document.getElementById('forecast'); forecast.innerHTML = '';
    data.forecast.forEach((day, index) => {
      const weather = codeInfo(day.weatherCode);
      const card = document.createElement('article'); card.className = 'day-card';
      card.innerHTML = `<div class="day-name">${dayName(day.date, index)} <span style="font-weight:400;color:#a0aabe">· ${day.date.slice(5).replace('-', '/')}</span></div><div class="day-icon">${weather[1]}</div><div class="day-desc">${weather[0]}</div><div class="temps"><span>${Math.round(day.max)}°</span><span class="low">${Math.round(day.min)}°</span></div><div class="rain-chance">☂ ${day.rainChance}% rain</div>`;
      forecast.appendChild(card);
    });
    const today = data.forecast[0]; document.getElementById('sunrise').textContent = timeOnly(today.sunrise); document.getElementById('sunset').textContent = timeOnly(today.sunset);
    message.textContent = ''; message.className = 'message hidden'; content.classList.remove('hidden');
  } catch (error) { message.className = 'message error'; message.textContent = error.message || 'Something went wrong. Please try again.'; }
}
form.addEventListener('submit', event => { event.preventDefault(); loadWeather(input.value.trim()); });
document.querySelectorAll('[data-city]').forEach(button => button.addEventListener('click', () => { input.value = button.dataset.city; loadWeather(button.dataset.city); }));
loadWeather(input.value);
