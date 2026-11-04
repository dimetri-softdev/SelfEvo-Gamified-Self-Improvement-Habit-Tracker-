const assert = require('node:assert/strict');
const { test } = require('node:test');
const { createProfileImageStorage } = require('../profile-image-storage');

const supabaseUrl = 'https://project.supabase.co';
const serviceRoleKey = 'server-only-test-key';

test('profile image storage is unavailable without server credentials', () => {
    assert.equal(createProfileImageStorage({ supabaseUrl, serviceRoleKey: '' }), null);
});

test('profile image storage refuses non-HTTPS production URLs', () => {
    assert.throws(
        () => createProfileImageStorage({ supabaseUrl: 'http://storage.example', serviceRoleKey }),
        /SUPABASE_URL must use HTTPS/
    );
});

test('profile image storage rejects invalid bucket names', () => {
    assert.throws(
        () => createProfileImageStorage({ supabaseUrl, serviceRoleKey, bucket: 'private/bucket' }),
        /SUPABASE_STORAGE_BUCKET is invalid/
    );
});

test('profile image upload uses the verified user path and server-only credentials', async () => {
    let request;
    const storage = createProfileImageStorage({
        supabaseUrl,
        serviceRoleKey,
        fetchImpl: async (url, options) => {
            request = { url, options };
            return new Response('{}', { status: 200 });
        }
    });
    const image = Buffer.from([1, 2, 3]);

    const path = await storage.uploadProfileImage('test-user', 'image/png', image);

    assert.equal(path, 'test-user/profile-image');
    assert.equal(
        request.url,
        'https://project.supabase.co/storage/v1/object/profile-images/test-user/profile-image'
    );
    assert.equal(request.options.method, 'POST');
    assert.equal(request.options.headers.Authorization, `Bearer ${serviceRoleKey}`);
    assert.equal(request.options.headers.apikey, serviceRoleKey);
    assert.equal(request.options.headers['Content-Type'], 'image/png');
    assert.equal(request.options.headers['x-upsert'], 'true');
    assert.deepEqual(request.options.body, image);
});

test('signed image URLs are time-limited and do not expose service credentials', async () => {
    let request;
    const storage = createProfileImageStorage({
        supabaseUrl,
        serviceRoleKey,
        fetchImpl: async (url, options) => {
            request = { url, options };
            return new Response(JSON.stringify({
                signedURL: '/object/sign/profile-images/test-user/profile-image?token=signed'
            }), { status: 200 });
        }
    });

    const url = await storage.createProfileImageSignedUrl('test-user');

    assert.equal(
        request.url,
        'https://project.supabase.co/storage/v1/object/sign/profile-images/test-user/profile-image'
    );
    assert.deepEqual(JSON.parse(request.options.body), { expiresIn: 3600 });
    assert.equal(url, `${supabaseUrl}/storage/v1/object/sign/profile-images/test-user/profile-image?token=signed`);
    assert.equal(url.includes(serviceRoleKey), false);
});

test('storage errors do not include response bodies that might contain credentials', async () => {
    const storage = createProfileImageStorage({
        supabaseUrl,
        serviceRoleKey,
        fetchImpl: async () => new Response('sensitive provider response', { status: 500 })
    });

    await assert.rejects(
        storage.uploadProfileImage('test-user', 'image/jpeg', Buffer.from([1])),
        /Supabase Storage upload failed with status 500/
    );
});
