const oneSecond = () => 1000;
const getCurrentTime = () => new Date();
const clear = () => console.clear();
const log = (message) => console.log(message);

// serialize date to clock time
const serializeClockTime = (date) => ({
    hours: date.getHours(),
    minutes: date.getMinutes(),
    seconds: date.getSeconds()
})

// turn clock time to civilian time
const civilianHours = (clockTime) => ({
    ...clockTime,
    hours: clockTime.hours > 12 ? clockTime.hours - 12 : clockTime.hours
})

// append AM/PM
const appendAPM = (clockTime) => ({
    ...clockTime,
    apm: clockTime.hours >= 12 ? 'PM': 'AM'
})

// here 'fn' will be console.log
const display = fn => time => fn(time);

// format clock with template string 'hh:mm:ss tt'
const formatClock = format => time => {
    return format.replace('hh', time.hours)
        .replace('mm', time.minutes)
        .replace('ss', time.seconds)
        .replace('tt', time.apm)
}

// prepend leading zero
const prependZero = key => clockTime => ({
    ...clockTime,
    [key]: clockTime[key] < 10 ? `0${clockTime[key]}` : clockTime[key]
})

// compose functional parts
const compose = (...fns) => arg => fns.reduce((composed, f) => f(composed), arg);

const convertToCivilianTime = clockTime => {
    return compose(
        appendAPM,
        civilianHours
    )(clockTime)
}

const doubleDigits = civilianTime =>
    compose(
        prependZero('hours'),
        prependZero('minutes'),
        prependZero('seconds')
    )(civilianTime)

const startTicking = () => {
    setInterval(
        compose(
            clear,
            getCurrentTime,
            serializeClockTime,
            convertToCivilianTime,
            doubleDigits,
            formatClock('hh:mm:ss tt'),
            display(log)
        ),
        oneSecond()
    )
}

export default startTicking;