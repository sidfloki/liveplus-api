<!DOCTYPE html>
<html <?php language_attributes(); ?> dir="rtl">
<head>
    <meta charset="<?php bloginfo('charset'); ?>">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <?php wp_head(); ?>
</head>
<body <?php body_class(); ?>>

<header class="site-header">
    <div class="container" style="display: flex; justify-content: space-between; align-items: center;">
        <div class="logo">
            <a href="<?php echo esc_url(home_url('/')); ?>" style="color: var(--primary); font-size: 2rem; font-weight: 900; text-decoration: none;">LIVEPLUS</a>
        </div>
        <nav class="nav-menu">
            <ul>
                <li><a href="<?php echo esc_url(home_url('/')); ?>">الرئيسية</a></li>
                <li><a href="<?php echo esc_url(home_url('/movie')); ?>">أفلام</a></li>
                <li><a href="<?php echo esc_url(home_url('/series')); ?>">مسلسلات</a></li>
                <li><a href="<?php echo esc_url(home_url('/anime')); ?>">أنمي</a></li>
            </ul>
        </nav>
    </div>
</header>
