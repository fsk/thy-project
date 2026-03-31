from faker import Faker


def generate_fake_users(count: int) -> list[dict]:
    fake = Faker("tr_TR")
    users = []

    for _ in range(count):
        users.append(
            {
                "full_name": fake.name(),
                "email": fake.email(),
                "phone": fake.phone_number(),
                "city": fake.city(),
                "company": fake.company(),
            }
        )

    return users
