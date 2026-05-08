<?php get_header(); ?>

<main class="site-main">
    <!-- Hero Section -->
    <section class="hero" style="height: 60vh; background: linear-gradient(rgba(0,0,0,0.6), var(--bg)), url('https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1350&q=80'); background-size: cover; background-position: center; display: flex; align-items: center;">
        <div class="container">
            <h1 style="font-size: 3.5rem; font-weight: 900; margin-bottom: 20px;">أهلاً بك في عالم الترفيه</h1>
            <p style="font-size: 1.2rem; max-width: 600px;">شاهد أحدث الأفلام والمسلسلات والأنمي بجودة عالية وسيرفرات سريعة.</p>
        </div>
    </section>

    <!-- Movie Grid -->
    <section class="container" style="margin-top: -50px; padding-bottom: 50px;">
        <div class="movie-grid">
            <?php if (have_posts()) : while (have_posts()) : the_post(); ?>
                <article class="movie-card">
                    <a href="<?php the_permalink(); ?>" style="text-decoration: none; color: inherit;">
                        <?php if (has_post_thumbnail()) : ?>
                            <?php the_post_thumbnail('medium'); ?>
                        <?php else : ?>
                            <img src="https://via.placeholder.com/200x300/1a1a1a/ffffff?text=No+Poster" alt="No Thumbnail">
                        <?php endif; ?>
                        <div class="info">
                            <h3 style="font-size: 1rem; margin-top: 10px;"><?php the_title(); ?></h3>
                        </div>
                    </a>
                </article>
            <?php endwhile; else : ?>
                <p>لا توجد أفلام حالياً.</p>
            <?php endif; ?>
        </div>
    </section>
</main>

<?php get_footer(); ?>
