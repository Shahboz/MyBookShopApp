'use strict';

let stylus = require('gulp-stylus');
let autoprefixer = require('gulp-autoprefixer');
let remember = require('gulp-remember');
let concat = require('gulp-concat');
let browserSync = require('browser-sync').create();
let gulp = require('gulp');

var path = require('path');
var sass = require('gulp-sass')(require('sass'));
var autoprefixer = require('gulp-autoprefixer');
var sourcemaps = require('gulp-sourcemaps');
var open = require('gulp-open');

browserSync.init({server: './'});

let way = {
    stylus: {
        basic: 'bricks/basic',
        extra: 'bricks/extra',
        tools: 'stylus',
        css: 'assets/css'
    }
};

let stylusBasic = () =>
    gulp.src(
        [
            way.stylus.tools + '/normalize.styl',
            way.stylus.basic + '/**/*.styl'
        ],
        {since: gulp.lastRun(stylusBasic)}
        )
    .pipe( stylus({
            import: [
                __dirname + '/' + way.stylus.tools + '/variables.styl',
                __dirname + '/' + way.stylus.tools + '/mixins.styl'
            ]
        }))
    .pipe(autoprefixer())
    .pipe(remember('basic'))
    .pipe(concat('basic.css'))
    .pipe( gulp.dest(way.stylus.css) )
    .pipe( browserSync.stream() ) ;

let stylusExtra = () =>
    gulp.src(
        [   way.stylus.extra + '/**/*.styl'],
        {since: gulp.lastRun(stylusExtra)}
        )
    .pipe( stylus({
            import: [
                __dirname + '/' + way.stylus.tools + '/variables.styl',
                __dirname + '/' + way.stylus.tools + '/mixins.styl'
            ]
        }))
    .pipe(autoprefixer())
    .pipe(remember('extra'))
    .pipe(concat('extra.css'))
    .pipe( gulp.dest(way.stylus.css) )
    .pipe( browserSync.stream() ) ;

let stylusWatch = () => {
    gulp.watch(
        [   way.stylus.basic + '/**/*.styl'],
        {delay: 80},
        stylusBasic
    );

    gulp.watch(
        [   way.stylus.extra + '/**/*.styl'],
        {delay: 80},
        stylusExtra
    );
};

var Paths = {
  HERE: './',
  DIST: 'dist/',
  CSS: './assets/css/',
  SCSS_TOOLKIT_SOURCES: './assets/scss/material-dashboard.scss',
  SCSS: './assets/scss/**/**'
};

gulp.task('compile-scss', function() {
  return gulp.src(Paths.SCSS_TOOLKIT_SOURCES)
    .pipe(sourcemaps.init())
    .pipe(sass().on('error', sass.logError))
    .pipe(autoprefixer())
    .pipe(sourcemaps.write(Paths.HERE))
    .pipe(gulp.dest(Paths.CSS));
});

gulp.task('watch', function() {
  gulp.watch(Paths.SCSS, gulp.series('compile-scss'));
});

gulp.task('open', function() {
  gulp.src('pages/dashboard.html')
    .pipe(open());
});

gulp.task('default', gulp.series(stylusBasic, stylusExtra, stylusWatch));
gulp.task('open-app', gulp.parallel('open', 'watch'));
